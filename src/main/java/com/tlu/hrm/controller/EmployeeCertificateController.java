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
	
	// =====================================================
    // HR
    // =====================================================

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        return ResponseEntity.ok(
                service.listAll(page, size, sort)
        );
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        return ResponseEntity.ok(
                service.search(keyword, page, size, sort)
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> create(@RequestBody EmployeeCertificateCreateDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody EmployeeCertificateUpdateDTO dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDetail(id));
    }

    // =====================================================
    // EMPLOYEE
    // =====================================================

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
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

    @GetMapping("/my/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<?> getMyCertificateDetail(@PathVariable Long id) {

        CustomUserDetails user =
                (CustomUserDetails) SecurityContextHolder
                        .getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(
                service.getMyCertificateDetail(user.getId(), id)
        );
    }
}
