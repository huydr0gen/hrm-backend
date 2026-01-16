package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.DepartmentApprovalViewDTO;
import com.tlu.hrm.dto.PersonalApprovalViewDTO;
import com.tlu.hrm.service.ApprovalConfigQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/approval-configs")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(
    name = "Approval Configuration",
    description = "Tra cứu cấu hình người duyệt theo phòng ban hoặc cá nhân"
)
public class ApprovalConfigQueryController {

	private final ApprovalConfigQueryService service;

	public ApprovalConfigQueryController(ApprovalConfigQueryService service) {
		super();
		this.service = service;
	}
	
	// =====================================================
    // GET – PHÒNG BAN
    // =====================================================

	@Operation(
	    summary = "Danh sách cấu hình người duyệt theo phòng ban",
	    description = """
	        Trả về danh sách cấu hình người duyệt theo phòng ban.

	        Quy ước:
	        - Mỗi phòng ban chỉ có một người duyệt duy nhất
	        - Chỉ trả về các cấu hình đang active
	        - Có phân trang
	        - Sắp xếp theo thời gian thiết lập mới nhất
	        """
	)
    @GetMapping("/departments")
    public ResponseEntity<Page<DepartmentApprovalViewDTO>>
    getDepartmentApprovals(
        @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
        @RequestParam int page,

        @Parameter(description = "Số bản ghi mỗi trang", example = "10")
        @RequestParam int size
    ) {
        return ResponseEntity.ok(
            service.getDepartmentApprovals(page, size)
        );
    }

    // =====================================================
    // GET – CÁ NHÂN
    // =====================================================

    @Operation(
	    summary = "Danh sách cấu hình người duyệt cá nhân",
	    description = """
	        Trả về danh sách các nhân viên đã được thiết lập người duyệt cá nhân.

	        Quy ước:
	        - Mỗi nhân viên chỉ có một người duyệt duy nhất
	        - Chỉ trả về các cấu hình đang active
	        - Có phân trang
	        - Sắp xếp theo thời gian thiết lập mới nhất
	        """
	)
    @GetMapping("/personal")
    public ResponseEntity<Page<PersonalApprovalViewDTO>>
    getPersonalApprovals(
        @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
        @RequestParam int page,

        @Parameter(description = "Số bản ghi mỗi trang", example = "10")
        @RequestParam int size
    ) {
        return ResponseEntity.ok(
            service.getPersonalApprovals(page, size)
        );
    }
}
