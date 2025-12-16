package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.DepartmentApprovalCreateDTO;
import com.tlu.hrm.dto.DepartmentApprovalDecisionDTO;
import com.tlu.hrm.dto.DepartmentApprovalResponseDTO;
import com.tlu.hrm.service.DepartmentApprovalService;

@RestController
@RequestMapping("/api/approvals/department")
public class DepartmentApprovalController {

	private final DepartmentApprovalService service;

	public DepartmentApprovalController(DepartmentApprovalService service) {
		super();
		this.service = service;
	}
	
	// =====================================================
    // CREATE DEPARTMENT APPROVAL (MANAGER)
    // =====================================================
    @PostMapping
    public ResponseEntity<DepartmentApprovalResponseDTO> create(
            @RequestBody DepartmentApprovalCreateDTO dto) {

        return ResponseEntity.ok(service.create(dto));
    }

    // =====================================================
    // GET DEPARTMENT APPROVALS (MANAGER)
    // =====================================================
    @GetMapping("/my-department")
    public ResponseEntity<Page<DepartmentApprovalResponseDTO>> getMyDepartmentApprovals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getDepartmentApprovals(page, size));
    }

    // =====================================================
    // GET ALL APPROVALS (HR / ADMIN)
    // =====================================================
    @GetMapping
    public ResponseEntity<Page<DepartmentApprovalResponseDTO>> getAllApprovals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getAllApprovals(page, size));
    }

    // =====================================================
    // DECIDE DEPARTMENT APPROVAL (HR / ADMIN)
    // =====================================================
    @PostMapping("/{id}/decision")
    public ResponseEntity<DepartmentApprovalResponseDTO> decide(
            @PathVariable Long id,
            @RequestBody DepartmentApprovalDecisionDTO dto) {

        return ResponseEntity.ok(service.decide(id, dto));
    }

    // =====================================================
    // DELETE DEPARTMENT APPROVAL (CREATOR & PENDING)
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
