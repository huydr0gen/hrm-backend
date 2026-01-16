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
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
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
	    summary = "Tạo hoặc cập nhật cấu hình người duyệt",
	    description = """
	        Cho phép ADMIN tạo mới hoặc cập nhật cấu hình người duyệt cho nhân viên hoặc phòng ban.

	        Quy ước xử lý:
	        - Nếu **không có id** → hệ thống sẽ tạo mới cấu hình
	        - Nếu **có id** → hệ thống sẽ cập nhật cấu hình tương ứng
	        - Một nhân viên hoặc một phòng ban chỉ được có **một người duyệt duy nhất**
	        - Nếu tạo mới mà target đã có người duyệt → hệ thống sẽ báo lỗi
	        - Nếu cập nhật và đổi target sang đối tượng đã có cấu hình → hệ thống sẽ báo lỗi

	        Áp dụng cho:
	        - Cá nhân (EMPLOYEE)
	        - Phòng ban (DEPARTMENT)
	        """
	)
	@PostMapping
    public ResponseEntity<ApprovalConfigDTO> createOrUpdate(
            @RequestBody ApprovalConfigCreateDTO dto) {

        return ResponseEntity.ok(service.createOrUpdate(dto));
    }

}
