package com.tlu.hrm.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.ApprovalConfigCreateDTO;
import com.tlu.hrm.dto.ApprovalConfigDTO;
import com.tlu.hrm.entities.ApprovalConfig;
import com.tlu.hrm.entities.AuditLog;
import com.tlu.hrm.repository.ApprovalConfigRepository;
import com.tlu.hrm.repository.AuditLogRepository;
import com.tlu.hrm.security.CustomUserDetails;

@Service
public class ApprovalConfigServiceImpl implements ApprovalConfigService {

	private final ApprovalConfigRepository approvalConfigRepository;
	private final AuditLogRepository auditLogRepository;
	
	public ApprovalConfigServiceImpl(ApprovalConfigRepository approvalConfigRepository,
			AuditLogRepository auditLogRepository) {
		super();
		this.approvalConfigRepository = approvalConfigRepository;
		this.auditLogRepository = auditLogRepository;
	}

	@Override
    public ApprovalConfigDTO createOrUpdate(ApprovalConfigCreateDTO dto) {

        if (dto.getTargetType() == null
                || dto.getTargetId() == null
                || dto.getApproverId() == null) {
            throw new RuntimeException("Thiếu dữ liệu thiết lập người duyệt");
        }

        ApprovalConfig config = approvalConfigRepository
                .findByTargetTypeAndTargetIdAndActiveTrue(
                        dto.getTargetType(),
                        dto.getTargetId()
                )
                .orElse(null);

        Long oldApproverId = null;
        String action;

        if (config == null) {
            // ===== CREATE =====
            config = new ApprovalConfig(
                    dto.getTargetType(),
                    dto.getTargetId(),
                    dto.getApproverId()
            );
            action = "APPROVAL_CONFIG_CREATE";
        } else {
            // ===== UPDATE =====
            oldApproverId = config.getApproverId();
            config.setApproverId(dto.getApproverId());
            action = "APPROVAL_CONFIG_UPDATE";
        }

        config.setActive(true);
        ApprovalConfig saved = approvalConfigRepository.save(config);

        // ==== GHI AUDIT LOG ====
        AuditLog log = new AuditLog();
        log.setUserId(getCurrentUserId());
        log.setAction(action);
        log.setDetails(buildAuditDetails(
                dto.getTargetType().name(),
                dto.getTargetId(),
                oldApproverId,
                dto.getApproverId()
        ));
        auditLogRepository.save(log);

        return mapToDTO(saved);
    }

    // =====================================================
    // Helper methods
    // =====================================================

    private ApprovalConfigDTO mapToDTO(ApprovalConfig config) {
        ApprovalConfigDTO dto = new ApprovalConfigDTO();
        dto.setId(config.getId());
        dto.setTargetType(config.getTargetType());
        dto.setTargetId(config.getTargetId());
        dto.setApproverId(config.getApproverId());
        dto.setActive(config.isActive());
        return dto;
    }

    private String buildAuditDetails(
            String targetType,
            Long targetId,
            Long oldApproverId,
            Long newApproverId
    ) {
        if (oldApproverId == null) {
            return String.format(
                    "Create approver for %s[%d] -> %d",
                    targetType,
                    targetId,
                    newApproverId
            );
        }
        return String.format(
                "Update approver for %s[%d]: %d -> %d",
                targetType,
                targetId,
                oldApproverId,
                newApproverId
        );
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUser().getId();
        }

        return null;
    }
}
