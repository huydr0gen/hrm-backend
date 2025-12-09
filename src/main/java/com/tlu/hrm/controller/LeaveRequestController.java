package com.tlu.hrm.controller;

import java.util.List;

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
	
	// CREATE (Employee)
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<LeaveRequestDTO> create(@RequestBody LeaveRequestCreateDTO dto) {
        dto.setEmployeeId(getCurrentEmployeeId());
        return ResponseEntity.ok(service.createRequest(dto));
    }

    // UPDATE (Employee)
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> update(
            @PathVariable Long id,
            @RequestBody LeaveRequestUpdateDTO dto) {
        return ResponseEntity.ok(service.updateRequest(id, dto));
    }

    // DELETE (Employee)
    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE (HR + Admin)
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteByAdmin(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @DeleteMapping("/admin/batch")
    public ResponseEntity<Void> deleteMany(@RequestBody List<Long> ids) {
        service.deleteMany(ids);
        return ResponseEntity.noContent().build();
    }

    // EMPLOYEE view own requests
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<?> myRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getMyRequests(getCurrentUserId(), page, size));
    }

    // MANAGER view department
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/department")
    public ResponseEntity<?> managerRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getDepartmentRequests(getCurrentUserId(), page, size));
    }

    // HR + Admin filter
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping
    public ResponseEntity<?> filter(
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getAllFiltered(employeeName, department, status, type, page, size));
    }

    // Approve / Reject
    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id, @RequestBody LeaveRequestDecisionDTO dto) {
        return ResponseEntity.ok(service.approve(id, dto));
    }

    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestBody LeaveRequestDecisionDTO dto) {
        return ResponseEntity.ok(service.reject(id, dto));
    }

    // Bulk approve/reject
    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PutMapping("/approve-batch")
    public ResponseEntity<Void> approveBatch(@RequestBody List<Long> ids) {
        service.approveMany(ids);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PutMapping("/reject-batch")
    public ResponseEntity<Void> rejectBatch(@RequestBody List<Long> ids) {
        service.rejectMany(ids);
        return ResponseEntity.ok().build();
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // Helpers
    private Long getCurrentUserId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private Long getCurrentEmployeeId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());
    }
}
