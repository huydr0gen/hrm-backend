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
            Trả về danh sách cấu hình người duyệt áp dụng cho phòng ban.

            Quy ước nghiệp vụ:
            - Mỗi phòng ban chỉ có tối đa 1 người duyệt
            - Một người có thể duyệt cho nhiều phòng ban
            - Người duyệt phải có role HR hoặc MANAGER
            - Không được chọn chính mình làm người duyệt

            Quy ước hiển thị:
            - Người duyệt được trả về dưới dạng chuỗi hiển thị:
              `username - EMPxxx - Full Name`

            Phân trang:
            - Có phân trang
            - Sắp xếp theo createdAt giảm dần (mới nhất trước)
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
            Trả về danh sách các nhân viên đã được thiết lập người duyệt riêng.

            Quy ước nghiệp vụ:
            - Mỗi nhân viên chỉ có tối đa 1 người duyệt
            - Một người có thể duyệt cho nhiều nhân viên
            - Có thể duyệt đồng thời cả phòng ban và cá nhân
            - Không được chọn chính mình làm người duyệt

            Quy ước phân quyền:
            - Nếu nhân viên cần duyệt là MANAGER → người duyệt phải có cả role MANAGER và HR
            - Nếu là nhân viên thường → người duyệt phải có MANAGER hoặc HR

            Quy ước hiển thị:
            - Người duyệt và người được duyệt đều được trả về dưới dạng:
              `username - EMPxxx - Full Name`

            Phân trang:
            - Có phân trang
            - Sắp xếp theo createdAt giảm dần (mới nhất trước)
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
