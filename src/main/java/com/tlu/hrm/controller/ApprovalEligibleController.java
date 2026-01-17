package com.tlu.hrm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.EligibleApproverDTO;
import com.tlu.hrm.enums.ApprovalTargetType;
import com.tlu.hrm.service.ApprovalEligibleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/approval-configs")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(
    name = "Approval Configuration – Eligible Approver",
    description = "API lấy danh sách người có thể được chọn làm người duyệt theo rule nghiệp vụ"
)
public class ApprovalEligibleController {

	private final ApprovalEligibleService approvalEligibleService;

	public ApprovalEligibleController(ApprovalEligibleService approvalEligibleService) {
		super();
		this.approvalEligibleService = approvalEligibleService;
	}
	
	@Operation(
        summary = "Danh sách người có thể được chọn làm người duyệt",
        description = """
            API này dùng để frontend lấy danh sách người có thể được chọn làm người duyệt
            dựa trên loại đối tượng cần duyệt và rule nghiệp vụ.

            Quy tắc lọc:

            1. Nếu targetType = EMPLOYEE:
               - Nếu nhân viên được duyệt có role MANAGER:
                 → Người duyệt phải có cả role HR và MANAGER
               - Nếu là nhân viên thường:
                 → Người duyệt phải có HR hoặc MANAGER

            2. Nếu targetType = DEPARTMENT:
               → Người duyệt phải có HR hoặc MANAGER

            Ràng buộc chung:
            - Không được chọn chính mình làm người duyệt
            - Không phân trang
            - Không thay đổi dữ liệu (read-only)

            Format hiển thị:
            - display = "username - EMPxxx - Full Name"
            """
    )
    @GetMapping("/eligible-approvers")
    public ResponseEntity<List<EligibleApproverDTO>> getEligibleApprovers(

        @Parameter(
            description = "Loại đối tượng cần thiết lập người duyệt",
            example = "EMPLOYEE",
            required = true
        )
        @RequestParam ApprovalTargetType targetType,

        @Parameter(
            description = "Mã đối tượng (employeeCode hoặc departmentCode)",
            example = "EMP005",
            required = true
        )
        @RequestParam String targetCode
    ) {
        return ResponseEntity.ok(
            approvalEligibleService.getEligibleApprovers(targetType, targetCode)
        );
    }
}
