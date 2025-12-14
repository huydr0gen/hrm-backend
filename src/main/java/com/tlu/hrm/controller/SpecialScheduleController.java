package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.BulkDecisionDTO;
import com.tlu.hrm.dto.BulkDecisionResultDTO;
import com.tlu.hrm.dto.SpecialScheduleCreateDTO;
import com.tlu.hrm.dto.SpecialScheduleDecisionDTO;
import com.tlu.hrm.dto.SpecialScheduleFilterDTO;
import com.tlu.hrm.dto.SpecialScheduleResponseDTO;
import com.tlu.hrm.dto.SpecialScheduleUpdateDTO;
import com.tlu.hrm.service.SpecialScheduleService;

@RestController
@RequestMapping("/special-schedules")
public class SpecialScheduleController {

	private final SpecialScheduleService specialScheduleService;

	public SpecialScheduleController(SpecialScheduleService specialScheduleService) {
		super();
		this.specialScheduleService = specialScheduleService;
	}
	
	// =====================================================
    // LIST (EMPLOYEE / MANAGER / HR / ADMIN)
    // =====================================================
    @PostMapping("/search")
    public ResponseEntity<Page<SpecialScheduleResponseDTO>> list(
            @RequestBody SpecialScheduleFilterDTO filter) {

        return ResponseEntity.ok(
                specialScheduleService.list(filter)
        );
    }

    // =====================================================
    // DETAIL
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<SpecialScheduleResponseDTO> detail(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                specialScheduleService.detail(id)
        );
    }

    // =====================================================
    // CREATE – EMPLOYEE
    // =====================================================
    @PostMapping
    public ResponseEntity<SpecialScheduleResponseDTO> create(
            @RequestBody SpecialScheduleCreateDTO dto) {

        return ResponseEntity.ok(
                specialScheduleService.create(dto)
        );
    }

    // =====================================================
    // UPDATE – HR / ADMIN
    // =====================================================
    @PutMapping("/{id}")
    public ResponseEntity<SpecialScheduleResponseDTO> update(
            @PathVariable Long id,
            @RequestBody SpecialScheduleUpdateDTO dto) {

        return ResponseEntity.ok(
                specialScheduleService.update(id, dto)
        );
    }

    // =====================================================
    // DECIDE – HR / MANAGER
    // =====================================================
    @PostMapping("/{id}/decision")
    public ResponseEntity<SpecialScheduleResponseDTO> decide(
            @PathVariable Long id,
            @RequestBody SpecialScheduleDecisionDTO dto) {

        return ResponseEntity.ok(
                specialScheduleService.decide(id, dto.getAction())
        );
    }

    // =====================================================
    // BULK DECIDE – HR / MANAGER
    // =====================================================
    @PostMapping("/decision/bulk")
    public ResponseEntity<BulkDecisionResultDTO> decideMany(
            @RequestBody BulkDecisionDTO dto) {

        return ResponseEntity.ok(
                specialScheduleService.decideMany(
                        dto.getIds(),
                        dto.getAction()
                )
        );
    }
}
