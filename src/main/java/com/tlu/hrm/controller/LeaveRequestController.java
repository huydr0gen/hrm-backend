package com.tlu.hrm.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.*;
import com.tlu.hrm.security.CustomUserDetails;
import com.tlu.hrm.service.LeaveRequestService;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

	private final LeaveRequestService service;

	public LeaveRequestController(LeaveRequestService service) {
		super();
		this.service = service;
	}
	
	// ---------------------------
    // Helper: resolve current user id from security context
    // ---------------------------
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails ud) {
            return ud.getId();
        }

        // sometimes principal is username (String) - attempt to parse as id
        if (principal instanceof String name) {
            try {
                return Long.parseLong(name);
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Cannot resolve current user id from principal");
            }
        }

        throw new RuntimeException("Cannot resolve current user id");
    }

    // ---------------------------
    // CREATE (Employee)
    // ---------------------------
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<LeaveRequestDTO> create(@RequestBody LeaveRequestCreateDTO dto) {
        // service will fallback to current user if dto.employeeId == null
        LeaveRequestDTO created = service.createRequest(dto);
        return ResponseEntity.ok(created);
    }

    // ---------------------------
    // EMPLOYEE - view own requests
    // ---------------------------
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<Page<LeaveRequestDTO>> myRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = getCurrentUserId();
        Page<LeaveRequestDTO> result = service.getMyRequests(userId, page, size);
        return ResponseEntity.ok(result);
    }

    // ---------------------------
    // MANAGER - view department requests
    // ---------------------------
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/department")
    public ResponseEntity<Page<LeaveRequestDTO>> managerRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long managerUserId = getCurrentUserId();
        Page<LeaveRequestDTO> result = service.getDepartmentRequests(managerUserId, page, size);
        return ResponseEntity.ok(result);
    }

    // ---------------------------
    // HR / ADMIN - list & filter
    // ---------------------------
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping
    public ResponseEntity<Page<LeaveRequestDTO>> filter(
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<LeaveRequestDTO> result = service.getAllFiltered(employeeName, department, status, type, page, size);
        return ResponseEntity.ok(result);
    }

    // ---------------------------
    // GET BY ID (any authenticated user who can access resource)
    // Note: consider restricting access by owner/manager/hr if needed
    // ---------------------------
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> getById(@PathVariable Long id) {
        LeaveRequestDTO dto = service.getById(id);
        return ResponseEntity.ok(dto);
    }

    // ---------------------------
    // ADMIN update (HR / ADMIN only)
    // ---------------------------
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<LeaveRequestDTO> adminUpdate(
            @PathVariable Long id,
            @RequestBody LeaveRequestUpdateDTO dto) {

        Long actorId = getCurrentUserId();
        LeaveRequestDTO updated = service.adminUpdate(id, dto, actorId);
        return ResponseEntity.ok(updated);
    }

    // ---------------------------
    // ADMIN delete (HR / ADMIN only)
    // ---------------------------
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteByAdmin(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @DeleteMapping("/admin/batch")
    public ResponseEntity<Void> deleteManyByAdmin(@RequestBody List<Long> ids) {
        service.deleteMany(ids);
        return ResponseEntity.noContent().build();
    }

    // ---------------------------
    // Approve / Reject => unified decision endpoint (Manager & HR)
    // ---------------------------
    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PatchMapping("/{id}/decision")
    public ResponseEntity<LeaveRequestDTO> decide(
            @PathVariable Long id,
            @RequestBody LeaveRequestDecisionDTO dto) {

        Long actorId = getCurrentUserId();
        LeaveRequestDTO result = service.decide(id, dto.getAction(), dto.getManagerNote(), actorId);
        return ResponseEntity.ok(result);
    }

    // ---------------------------
    // Bulk decision (Manager & HR)
    // ---------------------------
    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PatchMapping("/decision")
    public ResponseEntity<BulkDecisionResultDTO> decideMany(@RequestBody BulkDecisionDTO dto) {
        Long actorId = getCurrentUserId();
        BulkDecisionResultDTO result = service.decideMany(dto.getIds(), dto.getAction(), dto.getComment(), actorId);
        return ResponseEntity.ok(result);
    }
}
