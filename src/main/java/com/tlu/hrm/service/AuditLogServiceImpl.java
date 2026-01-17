package com.tlu.hrm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.AuditLogDTO;
import com.tlu.hrm.entities.AuditLog;
import com.tlu.hrm.repository.AuditLogRepository;

@Service
public class AuditLogServiceImpl implements AuditLogService {

	private final AuditLogRepository auditLogRepository;

	public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
		super();
		this.auditLogRepository = auditLogRepository;
	}
	
	@Override
    public void log(Long userId, String action, String details) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setDetails(details);

        auditLogRepository.save(log);
    }

    @Override
    public Page<AuditLogDTO> getLogs(Long userId, String action, String startDate, String endDate, int page, int size) {

    	PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        LocalDateTime start = (startDate != null) ? LocalDate.parse(startDate).atStartOfDay() : null;
        LocalDateTime end = (endDate != null) ? LocalDate.parse(endDate).atTime(23, 59, 59) : null;

        Page<AuditLog> logs;

        // 8 trường hợp filter
        if (userId != null && action != null && start != null && end != null) {
            logs = auditLogRepository.findByUserIdAndActionContainingIgnoreCaseAndCreatedAtBetween(userId, action, start, end, pageable);

        } else if (userId != null && action != null) {
            logs = auditLogRepository.findByUserIdAndActionContainingIgnoreCase(userId, action, pageable);

        } else if (userId != null && start != null && end != null) {
            logs = auditLogRepository.findByUserIdAndCreatedAtBetween(userId, start, end, pageable);

        } else if (action != null && start != null && end != null) {
            logs = auditLogRepository.findByActionContainingIgnoreCaseAndCreatedAtBetween(action, start, end, pageable);

        } else if (userId != null) {
            logs = auditLogRepository.findByUserId(userId, pageable);

        } else if (action != null) {
            logs = auditLogRepository.findByActionContainingIgnoreCase(action, pageable);

        } else if (start != null && end != null) {
            logs = auditLogRepository.findByCreatedAtBetween(start, end, pageable);

        } else {
            logs = auditLogRepository.findAll(pageable);
        }

        return new PageImpl<>(
            logs.getContent().stream().map(this::toDTO).toList(),
            pageable,
            logs.getTotalElements()
        );
    }

    private AuditLogDTO toDTO(AuditLog log) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(log.getId());
        dto.setUserId(log.getUserId());
        dto.setAction(log.getAction());
        dto.setDetails(log.getDetails());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }
}
