package com.tlu.hrm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.DepartmentApprovalViewDTO;
import com.tlu.hrm.dto.PersonalApprovalViewDTO;
import com.tlu.hrm.entities.Department;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.enums.ApprovalTargetType;
import com.tlu.hrm.repository.ApprovalConfigRepository;
import com.tlu.hrm.repository.DepartmentRepository;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.UserRepository;

@Service
public class ApprovalConfigQueryServiceImpl implements ApprovalConfigQueryService {

	private final ApprovalConfigRepository approvalConfigRepository;
	private final EmployeeRepository employeeRepository;
	private final DepartmentRepository departmentRepository;
	private final UserRepository userRepository;	

	
	
	public ApprovalConfigQueryServiceImpl(ApprovalConfigRepository approvalConfigRepository,
			EmployeeRepository employeeRepository, DepartmentRepository departmentRepository,
			UserRepository userRepository) {
		super();
		this.approvalConfigRepository = approvalConfigRepository;
		this.employeeRepository = employeeRepository;
		this.departmentRepository = departmentRepository;
		this.userRepository = userRepository;
	}

	// =====================================================
    // GET – PHÒNG BAN
    // =====================================================
    @Override
    public Page<DepartmentApprovalViewDTO> getDepartmentApprovals(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return approvalConfigRepository
                .findByTargetTypeAndActiveTrue(
                        ApprovalTargetType.DEPARTMENT,
                        pageable
                )
                .map(cfg -> {

                    DepartmentApprovalViewDTO dto = new DepartmentApprovalViewDTO();

                    // ===== TARGET DEPARTMENT =====
                    Department dept = departmentRepository
                            .findByCode(cfg.getTargetCode())
                            .orElse(null);

                    if (dept != null) {
                        dto.setDepartmentId(dept.getId());
                        dto.setDepartmentCode(dept.getCode());
                        dto.setDepartmentName(dept.getName());
                    } else {
                        dto.setDepartmentId(null);
                        dto.setDepartmentCode(cfg.getTargetCode());
                        dto.setDepartmentName("[DEPARTMENT NOT FOUND]");
                    }

                    // ===== APPROVER =====
                    Employee approver = employeeRepository
                            .findByCode(cfg.getApproverCode())
                            .orElse(null);

                    if (approver != null) {
                        dto.setApproverId(approver.getId());
                        dto.setApproverCode(approver.getCode());
                        dto.setApproverName(approver.getFullName());

                        String username = userRepository
                                .findByEmployee_Id(approver.getId())
                                .map(u -> u.getUsername())
                                .orElse(null);

                        dto.setApproverUsername(username);

                        String display = buildEmployeeDisplay(approver);
                        dto.setApproverDisplay(display);

                    } else {
                        dto.setApproverId(null);
                        dto.setApproverCode(cfg.getApproverCode());
                        dto.setApproverName("[APPROVER NOT FOUND]");
                        dto.setApproverUsername(null);
                        dto.setApproverDisplay("[APPROVER NOT FOUND]");
                    }

                    dto.setCreatedAt(cfg.getCreatedAt());

                    return dto;
                });
    }

    // =====================================================
    // GET – CÁ NHÂN
    // =====================================================
    @Override
    public Page<PersonalApprovalViewDTO> getPersonalApprovals(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return approvalConfigRepository
                .findByTargetTypeAndActiveTrue(
                        ApprovalTargetType.EMPLOYEE,
                        pageable
                )
                .map(cfg -> {

                    PersonalApprovalViewDTO dto = new PersonalApprovalViewDTO();

                    // ===== TARGET EMPLOYEE =====
                    Employee emp = employeeRepository
                            .findByCode(cfg.getTargetCode())
                            .orElse(null);

                    if (emp != null) {
                        dto.setEmployeeId(emp.getId());
                        dto.setEmployeeCode(emp.getCode());
                        dto.setEmployeeName(emp.getFullName());

                        String username = userRepository
                                .findByEmployee_Id(emp.getId())
                                .map(u -> u.getUsername())
                                .orElse(null);

                        dto.setTargetUsername(username);

                        String display = buildEmployeeDisplay(emp);
                        dto.setEmployeeDisplay(display);
                        
                    } else {
                        dto.setEmployeeId(null);
                        dto.setEmployeeCode(cfg.getTargetCode());
                        dto.setEmployeeName("[EMPLOYEE NOT FOUND]");
                        dto.setTargetUsername(null);
                        dto.setEmployeeDisplay("[EMPLOYEE NOT FOUND]");
                    }

                    // ===== APPROVER =====
                    Employee approver = employeeRepository
                            .findByCode(cfg.getApproverCode())
                            .orElse(null);

                    if (approver != null) {
                        dto.setApproverId(approver.getId());
                        dto.setApproverCode(approver.getCode());
                        dto.setApproverName(approver.getFullName());

                        String username = userRepository
                                .findByEmployee_Id(approver.getId())
                                .map(u -> u.getUsername())
                                .orElse(null);

                        dto.setApproverUsername(username);

                        String display = buildEmployeeDisplay(approver);
                        dto.setApproverDisplay(display);
                    } else {
                        dto.setApproverId(null);
                        dto.setApproverCode(cfg.getApproverCode());
                        dto.setApproverName("[APPROVER NOT FOUND]");
                        dto.setApproverUsername(null);
                        dto.setApproverDisplay("[APPROVER NOT FOUND]");
                    }

                    dto.setCreatedAt(cfg.getCreatedAt());

                    return dto;
                });
    }

    // =====================================================
    // DISPLAY BUILDER
    // =====================================================
    private String buildEmployeeDisplay(Employee emp) {
        return userRepository.findByEmployee_Id(emp.getId())
                .map(user ->
                        user.getUsername()
                                + " - "
                                + emp.getCode()
                                + " - "
                                + emp.getFullName()
                )
                .orElse(
                        emp.getCode() + " - " + emp.getFullName()
                );
    }
}
