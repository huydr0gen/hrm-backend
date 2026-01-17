package com.tlu.hrm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.EligibleApproverDTO;
import com.tlu.hrm.entities.Department;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.User;
import com.tlu.hrm.enums.ApprovalTargetType;
import com.tlu.hrm.repository.DepartmentRepository;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.UserRepository;

@Service
public class ApprovalEligibleServiceImpl implements ApprovalEligibleService {

	private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    
	public ApprovalEligibleServiceImpl(UserRepository userRepository, EmployeeRepository employeeRepository,
			DepartmentRepository departmentRepository) {
		super();
		this.userRepository = userRepository;
		this.employeeRepository = employeeRepository;
		this.departmentRepository = departmentRepository;
	}
    
	@Override
    public List<EligibleApproverDTO> getEligibleApprovers(
            ApprovalTargetType targetType,
            String targetCode
    ) {
        if (targetType == null || targetCode == null) {
            throw new RuntimeException("Thiếu targetType hoặc targetCode");
        }

        if (targetType == ApprovalTargetType.EMPLOYEE) {
            return getEligibleForEmployee(targetCode);
        } else {
            return getEligibleForDepartment(targetCode);
        }
    }

    // =====================================================
    // EMPLOYEE LOGIC
    // =====================================================
    private List<EligibleApproverDTO> getEligibleForEmployee(String employeeCode) {

        Employee targetEmployee = employeeRepository.findByCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        User targetUser = userRepository.findByEmployee_Id(targetEmployee.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user của nhân viên"));

        boolean targetIsManager = hasRole(targetUser, "MANAGER");

        return userRepository.findAll().stream()
                // Không cho chọn chính mình
                .filter(u -> u.getEmployee() != null)
                .filter(u -> !u.getEmployee().getId().equals(targetEmployee.getId()))

                // Lọc theo rule role
                .filter(u -> {
                    boolean isManager = hasRole(u, "MANAGER");
                    boolean isHr = hasRole(u, "HR");

                    if (targetIsManager) {
                        return isManager && isHr;
                    } else {
                        return isManager || isHr;
                    }
                })

                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // =====================================================
    // DEPARTMENT LOGIC
    // =====================================================
    private List<EligibleApproverDTO> getEligibleForDepartment(String departmentCode) {

        Department dept = departmentRepository.findByCode(departmentCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban"));

        return userRepository.findAll().stream()
                .filter(u -> u.getEmployee() != null)

                // HR hoặc MANAGER
                .filter(u -> hasRole(u, "HR") || hasRole(u, "MANAGER"))

                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // =====================================================
    // MAPPER
    // =====================================================
    private EligibleApproverDTO mapToDTO(User user) {
        Employee emp = user.getEmployee();

        EligibleApproverDTO dto = new EligibleApproverDTO();
        dto.setEmployeeId(emp.getId());
        dto.setEmployeeCode(emp.getCode());
        dto.setFullName(emp.getFullName());
        dto.setUsername(user.getUsername());

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName())
                .collect(Collectors.toList());

        dto.setRoles(roles);

        dto.setDisplay(buildDisplay(user.getUsername(), emp.getCode(), emp.getFullName()));

        return dto;
    }

    private String buildDisplay(String username, String code, String fullName) {
        return String.format("%s - %s - %s", username, code, fullName);
    }

    // =====================================================
    // ROLE CHECK
    // =====================================================
    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(roleName));
    }
    
}
