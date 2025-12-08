package com.tlu.hrm.service;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.AuditLogDTO;

public interface AuditLogService {

	void log(Long userId, String action, String details);

    Page<AuditLogDTO> getLogs(Long userId, String action, String startDate, String endDate, int page, int size);
}
