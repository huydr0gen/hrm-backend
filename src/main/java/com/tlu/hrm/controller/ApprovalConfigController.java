package com.tlu.hrm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.ApprovalConfigCreateDTO;
import com.tlu.hrm.dto.ApprovalConfigDTO;
import com.tlu.hrm.service.ApprovalConfigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/approval-configs")
@PreAuthorize("hasRole('ADMIN')")
@Tag(
	    name = "Approval Configuration",
	    description = "Thiết lập người duyệt cho nhân viên hoặc phòng ban"
	)
public class ApprovalConfigController {

	private final ApprovalConfigService service;

	public ApprovalConfigController(ApprovalConfigService service) {
		super();
		this.service = service;
	}
	
	@Operation(
        summary = "Thiết lập hoặc cập nhật người duyệt",
        description = """
            Cho phép ADMIN thiết lập hoặc thay đổi người duyệt.
            
            - Có thể thiết lập cho **cá nhân (EMPLOYEE)** hoặc **phòng ban (DEPARTMENT)**
            - Áp dụng cho **tất cả các loại đơn**
            - Nếu cấu hình đã tồn tại thì hệ thống sẽ **cập nhật người duyệt**
            """
    )
	@PostMapping
    public ResponseEntity<ApprovalConfigDTO> createOrUpdate(
            @RequestBody ApprovalConfigCreateDTO dto) {

        return ResponseEntity.ok(service.createOrUpdate(dto));
    }
}
