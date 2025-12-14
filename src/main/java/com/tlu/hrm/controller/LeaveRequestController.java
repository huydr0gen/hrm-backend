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
	
	// =====================================================
    // Helper: get current user id
    // =====================================================
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails ud) {
            return ud.getId();
        }

        if (principal instanceof String username) {
            try {
                return Long.parseLong(username);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cannot resolve user id");
            }
        }

        throw new RuntimeException("Cannot resolve user id");
    }

    // =====================================================
    // CREATE – EMPLOYEE
    // =====================================================
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<LeaveRequestDTO> create(
            @RequestBody LeaveRequestCreateDTO dto) {

        LeaveRequestDTO created = service.createRequest(dto);
        return ResponseEntity.ok(created);
    }

    // =====================================================
    // EMPLOYEE – MY REQUESTS
    // =====================================================
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<Page<LeaveRequestDTO>> myRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = getCurrentUserId();
        return ResponseEntity.ok(service.getMyRequests(userId, page, size));
    }

    // =====================================================
    // MANAGER – DEPARTMENT REQUESTS
    // =====================================================
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/department")
    public ResponseEntity<Page<LeaveRequestDTO>> departmentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long managerId = getCurrentUserId();
        return ResponseEntity.ok(service.getDepartmentRequests(managerId, page, size));
    }

    // =====================================================
    // HR / ADMIN – FILTER LIST
    // =====================================================
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping
    public ResponseEntity<Page<LeaveRequestDTO>> filter(
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.getAllFiltered(employeeName, department, status, type, page, size)
        );
    }

    // =====================================================
    // GET BY ID
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // =====================================================
    // HR / ADMIN – UPDATE
    // =====================================================
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<LeaveRequestDTO> adminUpdate(
            @PathVariable Long id,
            @RequestBody LeaveRequestUpdateDTO dto) {

        Long actorId = getCurrentUserId();
        return ResponseEntity.ok(service.adminUpdate(id, dto, actorId));
    }

    // =====================================================
    // HR / ADMIN – DELETE
    // =====================================================
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @DeleteMapping("/admin/batch")
    public ResponseEntity<Void> deleteMany(@RequestBody List<Long> ids) {
        service.deleteMany(ids);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // DECIDE – MANAGER / HR
    // =====================================================
    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PatchMapping("/{id}/decision")
    public ResponseEntity<LeaveRequestDTO> decide(
            @PathVariable Long id,
            @RequestBody LeaveRequestDecisionDTO dto) {

        Long actorId = getCurrentUserId();
        return ResponseEntity.ok(
                service.decide(id, dto.getAction(), dto.getManagerNote(), actorId)
        );
    }

    // =====================================================
    // BULK DECIDE – MANAGER / HR
    // ⚠ BulkDecisionDTO hiện KHÔNG có managerNote
    // → truyền null (đúng với Service hiện tại)
    // =====================================================
    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PatchMapping("/decision")
    public ResponseEntity<BulkDecisionResultDTO> decideMany(
            @RequestBody BulkDecisionDTO dto) {

        Long actorId = getCurrentUserId();
        return ResponseEntity.ok(
                service.decideMany(dto.getIds(), dto.getAction(), dto.getManagerNote(), actorId)
        );
    }
}
