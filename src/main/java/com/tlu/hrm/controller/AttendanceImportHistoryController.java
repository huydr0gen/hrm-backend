package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.AttendanceImportHistoryResponseDTO;
import com.tlu.hrm.service.AttendanceImportHistoryService;

@RestController
@RequestMapping("/api/attendance/import-histories")
public class AttendanceImportHistoryController {

	private final AttendanceImportHistoryService service;

	public AttendanceImportHistoryController(AttendanceImportHistoryService service) {
		super();
		this.service = service;
	}
	
	@GetMapping
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<Page<AttendanceImportHistoryResponseDTO>> getByMonth(
            @RequestParam String month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.getByMonth(month, page, size)
        );
    }
}
