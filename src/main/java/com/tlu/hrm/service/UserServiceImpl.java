package com.tlu.hrm.service;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlu.hrm.config.CompanyConfig;
import com.tlu.hrm.dto.ChangePasswordDTO;
import com.tlu.hrm.dto.UserCreateDTO;
import com.tlu.hrm.dto.UserDTO;
import com.tlu.hrm.dto.UserUpdateDTO;
import com.tlu.hrm.entities.ApprovalConfig;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.Role;
import com.tlu.hrm.entities.User;
import com.tlu.hrm.enums.ApprovalTargetType;
import com.tlu.hrm.enums.EmployeeStatus;
import com.tlu.hrm.enums.UserStatus;
import com.tlu.hrm.repository.ApprovalConfigRepository;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.RoleRepository;
import com.tlu.hrm.repository.UserRepository;
import com.tlu.hrm.security.CustomUserDetails;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	
	private final RoleRepository roleRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final EmployeeRepository employeeRepository;
	
	private final CompanyConfig companyConfig;
	
	private final AuditLogService auditLogService;
	
	private final ApprovalConfigRepository approvalConfigRepository;
	
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder, EmployeeRepository employeeRepository, 
			CompanyConfig companyConfig, AuditLogService auditLogService, 
			ApprovalConfigRepository approvalConfigRepository) {
		super();
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.employeeRepository = employeeRepository;
		this.companyConfig = companyConfig;
		this.auditLogService = auditLogService;
		this.approvalConfigRepository = approvalConfigRepository;
	}

	@Override
	public User createUserFromEmployee(Long employeeId) {

	    Employee employee = employeeRepository.findById(employeeId)
	            .orElseThrow(() -> new RuntimeException("Employee not found"));

	    // ❗ Prevent creating multiple users for one employee
	    if (employee.getUser() != null) {
	        throw new RuntimeException("Employee already has a user account");
	    }

	    String base = generateBaseUsername(employee.getFullName());
	    String username = generateUniqueUsername(base);

	    String email = username + "@" + companyConfig.getEmailDomain();

	    // Email generated when user account is created
	    employee.setEmail(email);

	    User user = new User();
	    user.setUsername(username);
	    user.setPassword(passwordEncoder.encode("1"));
	    user.setStatus(UserStatus.ACTIVE);
	    user.setEmployee(employee);

	    // 🔁 Set both sides of relation
	    employee.setUser(user);

	    // 🔐 Default role
	    Role employeeRole = roleRepository.findByName("EMPLOYEE")
	            .orElseThrow(() -> new RuntimeException("Role EMPLOYEE not found"));
	    user.setRoles(Set.of(employeeRole));

	    User saved = userRepository.save(user);
	    employeeRepository.save(employee);

	    auditLogService.log(
	            saved.getId(),
	            "CREATE_USER_FROM_EMPLOYEE",
	            "User auto-created for employee ID " + employeeId
	    );

	    return saved;
	}

    @Override
    public User createUser(UserCreateDTO dto) {

        if (existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(UserStatus.ACTIVE);

        if (dto.getRoleNames() != null) {
            Set<Role> roles = roleRepository.findByNameIn(dto.getRoleNames());
            user.setRoles(roles);
        }

        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            user.setEmployee(employee);
        }

        User saved = userRepository.save(user);

        // ⭐ LOG
        auditLogService.log(
                saved.getId(),
                "CREATE_USER",
                "User created: " + saved.getUsername()
        );

        return saved;
    }

    @Override
    public User updateUser(Long id, UserUpdateDTO dto) {
        User user = getUserById(id);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRoleNames() != null) {
            Set<Role> roles = roleRepository.findByNameIn(dto.getRoleNames());
            user.setRoles(roles);
        }

        User saved = userRepository.save(user);

        // ⭐ LOG
        auditLogService.log(
                saved.getId(),
                "UPDATE_USER",
                "User updated"
        );

        return saved;
    }

    @Override
    public void changeMyPassword(ChangePasswordDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        auditLogService.log(
                user.getId(),
                "CHANGE_PASSWORD",
                "User changed own password"
        );
    }

    @Override
    public void deleteUser(Long userIdToDelete) {

        // Lấy user đang đăng nhập (actor)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new RuntimeException("Invalid authentication principal");
        }

        Long actorUserId = userDetails.getId();

        // ⭐ LOG: ghi người THỰC HIỆN hành động
        auditLogService.log(
                actorUserId,
                "DELETE_USER",
                "Deleted user with id = " + userIdToDelete
        );

        // ⭐ SAU ĐÓ mới xóa user
        userRepository.deleteById(userIdToDelete);
    }

    @Override
    public User assignRoles(Long userId, Set<String> roleNames) {
        User user = getUserById(userId);
        Set<Role> roles = roleRepository.findByNameIn(roleNames);
        user.setRoles(roles);

        User saved = userRepository.save(user);

        // ⭐ LOG
        auditLogService.log(
                userId,
                "ASSIGN_ROLES",
                "Roles assigned: " + String.join(", ", roleNames)
        );

        return saved;
    }

    @Override
    public User resetPassword(Long userId) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode("1"));

        User saved = userRepository.save(user);

        // ⭐ LOG
        auditLogService.log(
                userId,
                "RESET_PASSWORD",
                "Password reset to default"
        );

        return saved;
    }

    @Override
    @Transactional
    public void activateUser(Long id) {
        User user = getUserById(id);

        if (user.getStatus() != UserStatus.ACTIVE) {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }

        if (user.getEmployee() != null) {
            Long empId = user.getEmployee().getId();
            Employee emp = employeeRepository.findById(empId).orElseThrow();
            emp.setStatus(EmployeeStatus.ACTIVE);
            employeeRepository.save(emp);
        }

        auditLogService.log(id, "ACTIVATE_USER", "Activated");
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        User user = getUserById(id);

        if (user.getStatus() != UserStatus.INACTIVE) {
            user.setStatus(UserStatus.INACTIVE);
            userRepository.save(user);
        }

        if (user.getEmployee() != null) {
            Long empId = user.getEmployee().getId();
            Employee emp = employeeRepository.findById(empId).orElseThrow();
            emp.setStatus(EmployeeStatus.INACTIVE);
            employeeRepository.save(emp);
        }

        auditLogService.log(id, "DEACTIVATE_USER", "Deactivated");
    }

    @Override
    @Transactional
    public void lockUser(Long id) {
        User user = getUserById(id);

        if (user.getStatus() != UserStatus.LOCKED) {
            user.setStatus(UserStatus.LOCKED);
            userRepository.save(user);
        }

        if (user.getEmployee() != null) {
            Long empId = user.getEmployee().getId();
            Employee emp = employeeRepository.findById(empId).orElseThrow();
            emp.setStatus(EmployeeStatus.LOCKED);
            employeeRepository.save(emp);
        }

        auditLogService.log(id, "LOCK_USER", "Account locked");
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void updateRefreshToken(Long userId, String refreshToken) {
        User user = getUserById(userId);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        auditLogService.log(userId, "REFRESH_TOKEN_UPDATE", "Refresh token updated");
    }

    @Override
    public Page<UserDTO> getUsers(int page, int size) {
        try {
            // ===== Validate input =====
            if (page < 0) page = 0;
            if (size <= 0) size = 10;

            Pageable pageable = PageRequest.of(page, size);
            Page<User> users = userRepository.findAllSortedByStatusAndTime(pageable);

            // ===============================
            // 1. Gom employeeIds + departmentIds
            // ===============================
            List<User> userList = users.getContent();

            List<Long> employeeIds = userList.stream()
                .filter(u -> u.getEmployee() != null)
                .map(u -> u.getEmployee().getId())
                .toList();

            Map<Long, Long> employeeDeptMap = userList.stream()
                .filter(u -> u.getEmployee() != null && u.getEmployee().getDepartment() != null)
                .collect(Collectors.toMap(
                    u -> u.getEmployee().getId(),
                    u -> u.getEmployee().getDepartment().getId(),
                    (a, b) -> a // chống duplicate key
                ));

            List<Long> departmentIds = employeeDeptMap.values().stream()
                .distinct()
                .toList();

            // ===============================
            // 2. Lấy cấu hình cá nhân
            // ===============================
            Map<Long, String> personalApproveMap = new HashMap<>();

            if (!employeeIds.isEmpty()) {
                List<ApprovalConfig> personalConfigs =
                    approvalConfigRepository.findByTargetTypeAndTargetIdInAndActiveTrue(
                        ApprovalTargetType.EMPLOYEE,
                        employeeIds
                    );

                personalApproveMap = personalConfigs.stream()
                    .collect(Collectors.toMap(
                        ApprovalConfig::getTargetId,
                        ApprovalConfig::getApproverCode,
                        (a, b) -> a
                    ));
            }

            // ===============================
            // 3. Lấy cấu hình phòng ban
            // ===============================
            Map<Long, String> departmentApproveMap = new HashMap<>();

            if (!departmentIds.isEmpty()) {
                List<ApprovalConfig> deptConfigs =
                    approvalConfigRepository.findByTargetTypeAndTargetIdInAndActiveTrue(
                        ApprovalTargetType.DEPARTMENT,
                        departmentIds
                    );

                departmentApproveMap = deptConfigs.stream()
                    .collect(Collectors.toMap(
                        ApprovalConfig::getTargetId,
                        ApprovalConfig::getApproverCode,
                        (a, b) -> a
                    ));
            }

            Map<Long, String> finalPersonalMap = personalApproveMap;
            Map<Long, String> finalDepartmentMap = departmentApproveMap;

            // ===============================
            // 4. Map DTO với logic fallback
            // ===============================
            return users.map(user -> {
                UserDTO dto = new UserDTO();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setStatus(user.getStatus());

                // ===== Null-safe roles =====
                if (user.getRoles() != null) {
                    dto.setRoles(
                        user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet())
                    );
                }

                if (user.getEmployee() != null) {
                    Long empId = user.getEmployee().getId();
                    dto.setEmployeeId(empId);

                    if (user.getEmployee().getCode() != null) {
                        dto.setEmpCode(user.getEmployee().getCode());
                    }

                    // ===== Ưu tiên cá nhân → fallback phòng ban =====
                    String approveCode = finalPersonalMap.get(empId);

                    if (approveCode == null) {
                        Long deptId = employeeDeptMap.get(empId);
                        if (deptId != null) {
                            approveCode = finalDepartmentMap.get(deptId);
                        }
                    }

                    dto.setApproveCode(approveCode);
                }

                dto.setLastLogin(user.getLastLogin());
                dto.setCreatedAt(user.getCreatedAt());
                dto.setUpdatedAt(user.getUpdatedAt());

                return dto;
            });

            
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh sách user: " + e.getMessage());
        }
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Generate username helpers
    private String generateBaseUsername(String fullName) {
        fullName = Normalizer.normalize(fullName.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d");;

        String[] parts = fullName.split("\\s+");
        String last = parts[parts.length - 1];

        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            initials.append(parts[i].charAt(0));
        }

        return last + initials.toString();
    }

    private String generateUniqueUsername(String base) {
        String username = base;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = base + counter;
            counter++;
        }

        return username;
    }
}
