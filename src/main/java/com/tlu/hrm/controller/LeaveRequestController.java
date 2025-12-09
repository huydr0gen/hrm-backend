package com.tlu.hrm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.*;
import com.tlu.hrm.service.LeaveRequestService;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

	private final LeaveRequestService service;

	public LeaveRequestController(LeaveRequestService service) {
		super();
		this.service = service;
	}
	
	// -----------------------------------------------------
    // EMPLOYEE: CREATE REQUEST
    // -----------------------------------------------------
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody LeaveRequestCreateDTO dto) {
        dto.setEmployeeId(getCurrentUserEmployeeId());
        return ResponseEntity.ok(service.createRequest(dto));
    }

    // -----------------------------------------------------
    // EMPLOYEE: UPDATE REQUEST
    // -----------------------------------------------------
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody LeaveRequestUpdateDTO dto) {
        return ResponseEntity.ok(service.updateRequest(id, dto));
    }

    // -----------------------------------------------------
    // EMPLOYEE: DELETE REQUEST
    // -----------------------------------------------------
    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------
    // EMPLOYEE: VIEW MY REQUESTS
    // -----------------------------------------------------
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<?> getMyRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getMyRequests(getCurrentUserId(), page, size));
    }

    // -----------------------------------------------------
    // MANAGER: VIEW DEPARTMENT REQUESTS
    // -----------------------------------------------------
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/department")
    public ResponseEntity<?> getDepartmentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getDepartmentRequests(getCurrentUserId(), page, size));
    }

    // -----------------------------------------------------
    // HR + ADMIN: FILTER view ALL company
    // -----------------------------------------------------
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping
    public ResponseEntity<?> filter(
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getAllFiltered(
                employeeName, department, status, type, page, size));
    }

    // -----------------------------------------------------
    // APPROVE (MANAGER, HR)
    // -----------------------------------------------------
    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Long id,
            @RequestBody LeaveRequestDecisionDTO dto) {
        return ResponseEntity.ok(service.approve(id, dto));
    }

    // -----------------------------------------------------
    // REJECT (MANAGER, HR)
    // -----------------------------------------------------
    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long id,
            @RequestBody LeaveRequestDecisionDTO dto) {
        return ResponseEntity.ok(service.reject(id, dto));
    }

    // -----------------------------------------------------
    // GET BY ID (any authenticated user)
    // -----------------------------------------------------
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // -----------------------------------------------------
    // HELPERS
    // -----------------------------------------------------
    private Long getCurrentUserId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private Long getCurrentUserEmployeeId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());
    }
}
