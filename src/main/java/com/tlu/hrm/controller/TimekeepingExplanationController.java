package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.BulkDecisionDTO;
import com.tlu.hrm.dto.BulkDecisionResultDTO;
import com.tlu.hrm.dto.TimekeepingExplanationCreateDTO;
import com.tlu.hrm.dto.TimekeepingExplanationDecisionDTO;
import com.tlu.hrm.dto.TimekeepingExplanationFilterDTO;
import com.tlu.hrm.dto.TimekeepingExplanationResponseDTO;
import com.tlu.hrm.service.TimekeepingExplanationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/timekeeping-explanations")
@Tag(name = "Timekeeping Explanation", description = "Quản lý giải trình công")
public class TimekeepingExplanationController {

	private final TimekeepingExplanationService service;

	public TimekeepingExplanationController(TimekeepingExplanationService service) {
		super();
		this.service = service;
	}
	
	// =====================================================
    // CREATE - Employee
    // =====================================================
    @Operation(summary = "Tạo yêu cầu giải trình công")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
  //@PreAuthorize("hasAnyAuthority('ROLE_HR','ROLE_EMPLOYEE')")
    public ResponseEntity<TimekeepingExplanationResponseDTO> create(
            @Valid @RequestBody TimekeepingExplanationCreateDTO dto
    ) {
        return ResponseEntity.ok(service.create(dto));
    }

    // =====================================================
    // LIST - HR / MANAGER / EMPLOYEE
    // =====================================================
    @Operation(summary = "Danh sách giải trình công (có filter + phân trang)")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<Page<TimekeepingExplanationResponseDTO>> getList(
            TimekeepingExplanationFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.getList(filter, page, size));
    }

    // =====================================================
    // DETAIL
    // =====================================================
    @Operation(summary = "Chi tiết 1 yêu cầu giải trình công")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<TimekeepingExplanationResponseDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // =====================================================
    // DECIDE ONE - HR / MANAGER
    // =====================================================
    @Operation(summary = "Phê duyệt / từ chối 1 yêu cầu giải trình công")
    @PatchMapping("/{id}/decision")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<TimekeepingExplanationResponseDTO> decide(
            @PathVariable Long id,
            @Valid @RequestBody TimekeepingExplanationDecisionDTO dto
    ) {
        return ResponseEntity.ok(service.decide(id, dto));
    }

    // =====================================================
    // DECIDE MANY - HR / MANAGER
    // =====================================================
    @Operation(summary = "Phê duyệt / từ chối nhiều yêu cầu giải trình công")
    @PatchMapping("/bulk-decision")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<BulkDecisionResultDTO> decideMany(
            @Valid @RequestBody BulkDecisionDTO dto
    ) {
        return ResponseEntity.ok(service.decideMany(dto));
    }
    
	 // =====================================================
	 // PENDING - HR / MANAGER
	 // =====================================================
    @Operation(
   	     summary = "Người duyệt xem giải trình công cần xử lý",
   	     description = """
   	         Áp dụng ApprovalConfig (cá nhân + phòng ban).
   	
   	         - Chỉ lấy lịch PENDING
   	         - Có phân trang
   	         """
   	 )
	 @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
	 public ResponseEntity<Page<TimekeepingExplanationResponseDTO>> getPending(
	         @RequestParam(defaultValue = "0") int page,
	         @RequestParam(defaultValue = "10") int size
	 ) {
	     return ResponseEntity.ok(service.getPending(page, size));
	 }
}
