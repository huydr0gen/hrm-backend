package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.SalaryImportHistoryResponseDTO;
import com.tlu.hrm.service.SalaryImportHistoryService;

@RestController
@RequestMapping("/api/salary/import-histories")
public class SalaryImportHistoryController {

	private final SalaryImportHistoryService service;

	public SalaryImportHistoryController(SalaryImportHistoryService service) {
		super();
		this.service = service;
	}
	
	@GetMapping
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<Page<SalaryImportHistoryResponseDTO>> getByMonth(
            @RequestParam String month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.getByMonth(month, page, size)
        );
    }
}
