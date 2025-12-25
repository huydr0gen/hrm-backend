package com.tlu.hrm.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tlu.hrm.config.CompanyConfig;
import com.tlu.hrm.dto.UserCreateDTO;
import com.tlu.hrm.dto.UserUpdateDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.Role;
import com.tlu.hrm.entities.User;
import com.tlu.hrm.enums.UserStatus;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.RoleRepository;
import com.tlu.hrm.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	
	private final RoleRepository roleRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final EmployeeRepository employeeRepository;
	
	private final CompanyConfig companyConfig;
	
	private final AuditLogService auditLogService;
	
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder, EmployeeRepository employeeRepository, 
			CompanyConfig companyConfig, AuditLogService auditLogService) {
		super();
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.employeeRepository = employeeRepository;
		this.companyConfig = companyConfig;
		this.auditLogService = auditLogService;
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

        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
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
    public void deleteUser(Long id) {

        // ⭐ LOG TRƯỚC
        auditLogService.log(
                id,
                "DELETE_USER",
                "User deleted"
        );

        // ⭐ SAU ĐÓ MỚI XÓA
        userRepository.deleteById(id);
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
    public void activateUser(Long id) {
        User user = getUserById(id);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        auditLogService.log(id, "ACTIVATE_USER", "Activated");
    }

    @Override
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        auditLogService.log(id, "DEACTIVATE_USER", "Deactivated");
    }

    @Override
    public void lockUser(Long id) {
        User user = getUserById(id);
        user.setStatus(UserStatus.LOCKED);
        userRepository.save(user);

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
    public Page<User> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
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
