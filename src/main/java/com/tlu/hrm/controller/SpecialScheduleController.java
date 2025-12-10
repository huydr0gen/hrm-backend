package com.tlu.hrm.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.SpecialScheduleApproveDTO;
import com.tlu.hrm.dto.SpecialScheduleBulkApproveDTO;
import com.tlu.hrm.dto.SpecialScheduleCreateDTO;
import com.tlu.hrm.dto.SpecialScheduleFilterDTO;
import com.tlu.hrm.dto.SpecialScheduleUpdateDTO;
import com.tlu.hrm.service.SpecialScheduleService;

@RestController
@RequestMapping("/special-schedules")
public class SpecialScheduleController {

	private final SpecialScheduleService service;

	public SpecialScheduleController(SpecialScheduleService service) {
		super();
		this.service = service;
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping
    public ResponseEntity<?> list(SpecialScheduleFilterDTO filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        return ResponseEntity.ok(service.list(filter, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody SpecialScheduleCreateDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SpecialScheduleUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id, @RequestBody SpecialScheduleApproveDTO dto) {
        return ResponseEntity.ok(service.approve(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/approve-many")
    public ResponseEntity<?> approveMany(@RequestBody SpecialScheduleBulkApproveDTO dto) {
        return ResponseEntity.ok(service.approveMany(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return ResponseEntity.ok(service.detail(id));
    }
}
