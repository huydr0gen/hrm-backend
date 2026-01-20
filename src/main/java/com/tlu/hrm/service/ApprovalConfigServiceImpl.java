package com.tlu.hrm.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.ApprovalConfigCreateDTO;
import com.tlu.hrm.dto.ApprovalConfigDTO;
import com.tlu.hrm.entities.ApprovalConfig;
import com.tlu.hrm.entities.AuditLog;
import com.tlu.hrm.entities.Department;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.User;
import com.tlu.hrm.enums.ApprovalTargetType;
import com.tlu.hrm.repository.ApprovalConfigRepository;
import com.tlu.hrm.repository.AuditLogRepository;
import com.tlu.hrm.repository.DepartmentRepository;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.UserRepository;
import com.tlu.hrm.security.CustomUserDetails;

@Service
public class ApprovalConfigServiceImpl implements ApprovalConfigService {

	private final ApprovalConfigRepository approvalConfigRepository;
	private final AuditLogRepository auditLogRepository;
	private final EmployeeRepository employeeRepository;
	private final DepartmentRepository departmentRepository;
	private final UserRepository userRepository;

	public ApprovalConfigServiceImpl(ApprovalConfigRepository approvalConfigRepository,
			AuditLogRepository auditLogRepository, EmployeeRepository employeeRepository,
			DepartmentRepository departmentRepository, UserRepository userRepository) {
		super();
		this.approvalConfigRepository = approvalConfigRepository;
		this.auditLogRepository = auditLogRepository;
		this.employeeRepository = employeeRepository;
		this.departmentRepository = departmentRepository;
		this.userRepository = userRepository;
	}

	@Override
	public ApprovalConfigDTO createOrUpdate(ApprovalConfigCreateDTO dto) {

	    if (dto.getTargetType() == null
	            || dto.getTargetCode() == null
	            || dto.getApproverCode() == null) {
	        throw new RuntimeException("Thiếu dữ liệu thiết lập người duyệt");
	    }

	    // =====================================================
	    // Resolve TARGET (CODE → ID)
	    // =====================================================
	    Long targetId;
	    String targetName;

	    if (dto.getTargetType() == ApprovalTargetType.EMPLOYEE) {
	        Employee emp = employeeRepository.findByCode(dto.getTargetCode())
	                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
	        targetId = emp.getId();
	        targetName = emp.getFullName();
	    } else {
	        Department dept = departmentRepository.findByCode(dto.getTargetCode())
	                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban"));
	        targetId = dept.getId();
	        targetName = dept.getName();
	    }

	    // =====================================================
	    // Resolve APPROVER (CODE → ID)
	    // =====================================================
	    Employee approver = employeeRepository.findByCode(dto.getApproverCode())
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy người duyệt"));

	    Long approverId = approver.getId();

	    // =====================================================
	    // ROLE VALIDATION (ONLY FOR PERSONAL)
	    // =====================================================
	    if (dto.getTargetType() == ApprovalTargetType.EMPLOYEE) {

	        User targetUser = userRepository.findByEmployee_Id(targetId)
	                .orElseThrow(() -> new RuntimeException("Không tìm thấy user của nhân viên cần duyệt"));

	        User approverUser = userRepository.findByEmployee_Id(approverId)
	                .orElseThrow(() -> new RuntimeException("Không tìm thấy user của người duyệt"));

	        boolean targetIsManager = hasRole(targetUser, "MANAGER");

	        boolean approverHasManager = hasRole(approverUser, "MANAGER");
	        boolean approverHasHr = hasRole(approverUser, "HR");

	        if (targetIsManager) {
	            if (!(approverHasManager && approverHasHr)) {
	                throw new RuntimeException("Người duyệt phải có cả role MANAGER và HR");
	            }
	        } else {
	            if (!(approverHasManager || approverHasHr)) {
	                throw new RuntimeException("Người duyệt phải là MANAGER hoặc HR");
	            }
	        }
	    }

	    ApprovalConfig config;
	    String oldApproverCode = null;
	    String action;

	    // =====================================================
	    // CREATE
	    // =====================================================
	    if (dto.getId() == null) {

	        boolean existed = approvalConfigRepository
	                .existsByTargetTypeAndTargetIdAndActiveTrue(
	                        dto.getTargetType(),
	                        targetId
	                );

	        if (existed) {
	        	throw new RuntimeException(
	        		    String.format("%s [%s] đã có người duyệt rồi",
	        		        dto.getTargetType().name(),
	        		        dto.getTargetCode()
	        		    )
	        		);
	        }

	        config = new ApprovalConfig(
	                dto.getTargetType(),
	                targetId,
	                approverId,
	                dto.getTargetCode(),
	                dto.getApproverCode()
	        );

	        action = "APPROVAL_CONFIG_CREATE";
	    }

	    // =====================================================
	    // UPDATE
	    // =====================================================
	    else {

	        config = approvalConfigRepository.findById(dto.getId())
	                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình người duyệt"));

	        boolean isTargetChanged =
	                !config.getTargetType().equals(dto.getTargetType())
	                        || !config.getTargetId().equals(targetId);

	        if (isTargetChanged) {
	            boolean existed = approvalConfigRepository
	                    .existsByTargetTypeAndTargetIdAndActiveTrue(
	                            dto.getTargetType(),
	                            targetId
	                    );

	            if (existed) {
	            	throw new RuntimeException(
	            		    String.format("%s [%s] đã có người duyệt rồi",
	            		        dto.getTargetType().name(),
	            		        dto.getTargetCode()
	            		    )
	            		);
	            }
	        }

	        oldApproverCode = config.getApproverCode();

	        config.setTargetType(dto.getTargetType());
	        config.setTargetId(targetId);
	        config.setTargetCode(dto.getTargetCode());
	        config.setApproverId(approverId);
	        config.setApproverCode(dto.getApproverCode());

	        action = "APPROVAL_CONFIG_UPDATE";
	    }

	    ApprovalConfig saved = approvalConfigRepository.save(config);

	    // =====================================================
	    // AUDIT LOG
	    // =====================================================
	    AuditLog log = new AuditLog();
	    log.setUserId(getCurrentUserId());
	    log.setAction(action);
	    log.setDetails(buildAuditDetails(
	            dto.getTargetType().name(),
	            dto.getTargetCode(),
	            oldApproverCode,
	            dto.getApproverCode()
	    ));
	    auditLogRepository.save(log);

	    return mapToDTO(saved);
	}
	
	@Override
	public void deleteById(Long id) {
	    ApprovalConfig config = approvalConfigRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình người duyệt"));

	    approvalConfigRepository.delete(config);

	    AuditLog log = new AuditLog();
	    log.setUserId(getCurrentUserId());
	    log.setAction("APPROVAL_CONFIG_DELETE");
	    log.setDetails(String.format(
	        "Delete approver for %s[%s] -> %s",
	        config.getTargetType().name(),
	        config.getTargetCode(),
	        config.getApproverCode()
	    ));

	    auditLogRepository.save(log);
	}

    // =====================================================
    // ROLE CHECK HELPER
    // =====================================================
    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(roleName));
    }

    // =====================================================
    // DTO MAPPER
    // =====================================================
    private ApprovalConfigDTO mapToDTO(ApprovalConfig config) {
        ApprovalConfigDTO dto = new ApprovalConfigDTO();
        dto.setId(config.getId());
        dto.setTargetType(config.getTargetType());
        dto.setTargetId(config.getTargetId());
        dto.setApproverId(config.getApproverId());
        dto.setTargetCode(config.getTargetCode());
        dto.setApproverCode(config.getApproverCode());
        dto.setActive(config.isActive());

        // ===== TARGET =====
        if (config.getTargetType() == ApprovalTargetType.EMPLOYEE) {
            Employee targetEmp = employeeRepository
                    .findByCode(config.getTargetCode())
                    .orElse(null);

            if (targetEmp != null) {
                dto.setTargetName(targetEmp.getFullName());

                User targetUser = userRepository
                        .findByEmployee_Id(targetEmp.getId())
                        .orElse(null);

                if (targetUser != null) {
                	dto.setTargetUsername(targetUser.getUsername());
                    dto.setTargetDisplay(
                            buildDisplay(
                                    targetUser.getUsername(),
                                    targetEmp.getCode(),
                                    targetEmp.getFullName()
                            )
                    );
                }
            }
        } else {
            Department dept = departmentRepository
                    .findByCode(config.getTargetCode())
                    .orElse(null);

            if (dept != null) {
                dto.setTargetName(dept.getName());
            }
        }

        // ===== APPROVER =====
        Employee approverEmp = employeeRepository
                .findByCode(config.getApproverCode())
                .orElse(null);

        if (approverEmp != null) {
            dto.setApproverName(approverEmp.getFullName());

            User approverUser = userRepository
                    .findByEmployee_Id(approverEmp.getId())
                    .orElse(null);

            if (approverUser != null) {
            	dto.setApproverUsername(approverUser.getUsername());
                dto.setApproverDisplay(
                        buildDisplay(
                                approverUser.getUsername(),
                                approverEmp.getCode(),
                                approverEmp.getFullName()
                        )
                );
            }
        }

        return dto;
    }

    // =====================================================
    // DISPLAY BUILDER
    // =====================================================
    private String buildDisplay(String username, String code, String fullName) {
        return String.format("%s - %s - %s", username, code, fullName);
    }

    private String buildAuditDetails(
            String targetType,
            String targetCode,
            String oldApproverCode,
            String newApproverCode
    ) {
        if (oldApproverCode == null) {
            return String.format(
                    "Create approver for %s[%s] -> %s",
                    targetType,
                    targetCode,
                    newApproverCode
            );
        }

        return String.format(
                "Update approver for %s[%s]: %s -> %s",
                targetType,
                targetCode,
                oldApproverCode,
                newApproverCode
        );
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        return null;
    }
}
