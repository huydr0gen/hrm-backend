package com.tlu.hrm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.EmployeeCertificateCreateDTO;
import com.tlu.hrm.dto.EmployeeCertificateUpdateDTO;
import com.tlu.hrm.security.CustomUserDetails;
import com.tlu.hrm.service.EmployeeCertificateService;

@RestController
@RequestMapping("/api/certificates")
public class EmployeeCertificateController {

	private final EmployeeCertificateService service;

	public EmployeeCertificateController(EmployeeCertificateService service) {
		super();
		this.service = service;
	}
	
	// ===== HR =====
    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<?> create(@RequestBody EmployeeCertificateCreateDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody EmployeeCertificateUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<?> getByEmployee(
            @RequestParam Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.getByEmployee(employeeId, page, size)
        );
    }

    // ===== EMPLOYEE =====
    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> getMyCertificates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        CustomUserDetails user =
                (CustomUserDetails) SecurityContextHolder
                        .getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(
                service.getMyCertificates(user.getId(), page, size)
        );
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDetail(id));
    }
    
    @GetMapping("/my/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> getMyCertificateDetail(@PathVariable Long id) {

        CustomUserDetails user =
                (CustomUserDetails) SecurityContextHolder
                        .getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(
                service.getMyCertificateDetail(user.getId(), id)
        );
    }
}
