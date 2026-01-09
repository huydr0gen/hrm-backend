package com.tlu.hrm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.OTRequestCreateDTO;
import com.tlu.hrm.dto.OTResponseDTO;
import com.tlu.hrm.enums.OTRequestStatus;
import com.tlu.hrm.service.OTService;

@RestController
@RequestMapping("/api/ots")
public class OTController {

	private final OTService service;

	public OTController(OTService service) {
		super();
		this.service = service;
	}
	
	@PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<?> create(@RequestBody OTRequestCreateDTO dto) {
        return ResponseEntity.ok(service.createOT(dto));
    }

    @PatchMapping("/participants/{id}/response")
    public ResponseEntity<?> respond(@PathVariable Long id,
                                     @RequestBody OTResponseDTO dto) {
        service.respondOT(id, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<?> myOTs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.getMyOTs(page, size));
    }
    
    @GetMapping("/manager")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<?> managerOTs(
            @RequestParam(required = false) OTRequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                service.getManagerOTs(status, page, size)
        );
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        service.cancelOT(id);
        return ResponseEntity.ok().build();
    }
}
