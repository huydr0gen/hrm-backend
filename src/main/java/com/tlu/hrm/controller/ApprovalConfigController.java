package com.tlu.hrm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.ApprovalConfigCreateDTO;
import com.tlu.hrm.dto.ApprovalConfigDTO;
import com.tlu.hrm.service.ApprovalConfigService;

@RestController
@RequestMapping("/api/approval-configs")
@PreAuthorize("hasRole('HR')")
public class ApprovalConfigController {

	private final ApprovalConfigService service;

	public ApprovalConfigController(ApprovalConfigService service) {
		super();
		this.service = service;
	}
	
	@PostMapping
    public ResponseEntity<ApprovalConfigDTO> createOrUpdate(
            @RequestBody ApprovalConfigCreateDTO dto) {

        return ResponseEntity.ok(service.createOrUpdate(dto));
    }
}
