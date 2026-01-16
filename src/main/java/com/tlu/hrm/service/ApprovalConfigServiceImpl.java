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
import com.tlu.hrm.enums.ApprovalTargetType;
import com.tlu.hrm.repository.ApprovalConfigRepository;
import com.tlu.hrm.repository.AuditLogRepository;
import com.tlu.hrm.repository.DepartmentRepository;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.security.CustomUserDetails;

@Service
public class ApprovalConfigServiceImpl implements ApprovalConfigService {

	private final ApprovalConfigRepository approvalConfigRepository;
	private final AuditLogRepository auditLogRepository;
	private final EmployeeRepository employeeRepository;
	private final DepartmentRepository departmentRepository;

	public ApprovalConfigServiceImpl(ApprovalConfigRepository approvalConfigRepository,
			AuditLogRepository auditLogRepository, EmployeeRepository employeeRepository,
			DepartmentRepository departmentRepository) {
		super();
		this.approvalConfigRepository = approvalConfigRepository;
		this.auditLogRepository = auditLogRepository;
		this.employeeRepository = employeeRepository;
		this.departmentRepository = departmentRepository;
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

	        // Nếu đổi target thì phải check trùng
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

	    return mapToDTO(saved, targetName, approver.getFullName());
	}

    // =====================================================
    // DTO MAPPER
    // =====================================================
    private ApprovalConfigDTO mapToDTO(
            ApprovalConfig config,
            String targetName,
            String approverName
    ) {
        ApprovalConfigDTO dto = new ApprovalConfigDTO();
        dto.setId(config.getId());
        dto.setTargetType(config.getTargetType());
        dto.setTargetCode(config.getTargetCode());
        dto.setTargetName(targetName);
        dto.setApproverCode(config.getApproverCode());
        dto.setApproverName(approverName);
        dto.setActive(config.isActive());
        return dto;
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
