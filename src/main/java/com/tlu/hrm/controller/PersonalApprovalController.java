package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.PersonalApprovalCreateDTO;
import com.tlu.hrm.dto.PersonalApprovalDecisionDTO;
import com.tlu.hrm.dto.PersonalApprovalResponseDTO;
import com.tlu.hrm.dto.PersonalApprovalUpdateDTO;
import com.tlu.hrm.service.PersonalApprovalService;

@RestController
@RequestMapping("/api/approvals/personal")
public class PersonalApprovalController {
	
	private final PersonalApprovalService service;

	public PersonalApprovalController(PersonalApprovalService service) {
		super();
		this.service = service;
	}

	// =====================================================
    // CREATE PERSONAL APPROVAL
    // =====================================================
    @PostMapping
    public ResponseEntity<PersonalApprovalResponseDTO> create(
            @RequestBody PersonalApprovalCreateDTO dto) {

        return ResponseEntity.ok(service.create(dto));
    }

    // =====================================================
    // GET MY APPROVALS (EMPLOYEE)
    // =====================================================
    @GetMapping("/my")
    public ResponseEntity<Page<PersonalApprovalResponseDTO>> getMyApprovals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getMyApprovals(page, size));
    }

    // =====================================================
    // GET DEPARTMENT APPROVALS (MANAGER)
    // =====================================================
    @GetMapping("/department")
    public ResponseEntity<Page<PersonalApprovalResponseDTO>> getDepartmentApprovals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getDepartmentApprovals(page, size));
    }

    // =====================================================
    // DECIDE APPROVAL (APPROVE / REJECT)
    // =====================================================
    @PostMapping("/{id}/decision")
    public ResponseEntity<PersonalApprovalResponseDTO> decide(
            @PathVariable Long id,
            @RequestBody PersonalApprovalDecisionDTO dto) {

        return ResponseEntity.ok(service.decide(id, dto));
    }
    
    // =====================================================
    // UPDATE PERSONAL APPROVAL
    // =====================================================
    @PutMapping("/{id}")
    public ResponseEntity<PersonalApprovalResponseDTO> update(
            @PathVariable Long id,
            @RequestBody PersonalApprovalUpdateDTO dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    // =====================================================
    // DELETE PERSONAL APPROVAL
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
