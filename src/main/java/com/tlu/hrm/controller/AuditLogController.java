package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.AuditLogDTO;
import com.tlu.hrm.service.AuditLogService;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

	private final AuditLogService service;


	public AuditLogController(AuditLogService service) {
		super();
		this.service = service;
	}



	@PreAuthorize("hasAnyAuthority('ROLE_HR','ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<AuditLogDTO>> getLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String startDate,   // yyyy-MM-dd
            @RequestParam(required = false) String endDate,     // yyyy-MM-dd
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.getLogs(userId, action, startDate, endDate, page, size));
    }
}
