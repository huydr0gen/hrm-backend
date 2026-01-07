package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.BulkDecisionDTO;
import com.tlu.hrm.dto.BulkDecisionResultDTO;
import com.tlu.hrm.dto.SpecialScheduleCreateDTO;
import com.tlu.hrm.dto.SpecialScheduleDecisionDTO;
import com.tlu.hrm.dto.SpecialScheduleResponseDTO;
import com.tlu.hrm.dto.SpecialScheduleUpdateDTO;
import com.tlu.hrm.service.SpecialScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Special Schedule",
    description = "Quản lý lịch làm việc đặc thù (thai sản, on-site, con nhỏ, lịch đặc biệt)"
)
@RestController
@RequestMapping("/api/special-schedules")
public class SpecialScheduleController {

	private final SpecialScheduleService specialScheduleService;

	public SpecialScheduleController(SpecialScheduleService specialScheduleService) {
		super();
		this.specialScheduleService = specialScheduleService;
	}
	
	// =====================================================
    // VIEW MY SCHEDULES – EMPLOYEE
    // =====================================================
    @Operation(
        summary = "Nhân viên xem toàn bộ lịch đặc thù của mình",
        description = """
            Role:
            - EMPLOYEE

            Nghiệp vụ:
            - Trả về toàn bộ lịch do nhân viên tạo
            - Bao gồm mọi trạng thái
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/my")
    public ResponseEntity<Page<SpecialScheduleResponseDTO>> getMySchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                specialScheduleService.getMySchedules(page, size)
        );
    }

    // =====================================================
    // VIEW DEPARTMENT – MANAGER
    // =====================================================
    @Operation(
        summary = "Quản lý xem lịch đặc thù của nhân viên trong phòng ban",
        description = """
            Role:
            - MANAGER

            Nghiệp vụ:
            - Chỉ xem lịch của nhân viên thuộc phòng ban mình quản lý
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/department")
    public ResponseEntity<Page<SpecialScheduleResponseDTO>> getDepartmentSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                specialScheduleService.getDepartmentSchedules(page, size)
        );
    }

    // =====================================================
    // DETAIL
    // =====================================================
    @Operation(
        summary = "Xem chi tiết một lịch đặc thù",
        description = """
            Role:
            - EMPLOYEE / MANAGER / APPROVER

            Ghi chú:
            - Phân quyền được kiểm soát ở Service
            """
    )
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
    @Operation(
        summary = "Nhân viên tạo lịch làm việc đặc thù",
        description = """
            Role:
            - EMPLOYEE

            Nghiệp vụ:
            - Lịch được tạo ở trạng thái PENDING
            - Người duyệt được xác định theo cấu hình duyệt
            """
    )
    @PostMapping
    public ResponseEntity<SpecialScheduleResponseDTO> create(
            @RequestBody SpecialScheduleCreateDTO dto) {

        return ResponseEntity.ok(
                specialScheduleService.create(dto)
        );
    }

    // =====================================================
    // UPDATE – EMPLOYEE (OWN + PENDING)
    // =====================================================
    @Operation(
        summary = "Nhân viên chỉnh sửa lịch đặc thù",
        description = """
            Role:
            - EMPLOYEE

            Điều kiện:
            - Chỉ sửa lịch của chính mình
            - Chỉ khi trạng thái là PENDING
            """
    )
    @PutMapping("/{id}")
    public ResponseEntity<SpecialScheduleResponseDTO> update(
            @PathVariable Long id,
            @RequestBody SpecialScheduleUpdateDTO dto) {

        return ResponseEntity.ok(
                specialScheduleService.update(id, dto)
        );
    }

    // =====================================================
    // DELETE – EMPLOYEE (OWN + PENDING)
    // =====================================================
    @Operation(
        summary = "Nhân viên huỷ lịch đặc thù",
        description = """
            Role:
            - EMPLOYEE

            Điều kiện:
            - Chỉ huỷ lịch của chính mình
            - Chỉ khi trạng thái là PENDING
            """
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        specialScheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // DECIDE – APPROVER
    // =====================================================
    @Operation(
        summary = "Duyệt hoặc từ chối lịch đặc thù",
        description = """
            Role:
            - Người được thiết lập là approver

            Action:
            - APPROVE
            - REJECT
            """
    )
    @PostMapping("/{id}/decision")
    public ResponseEntity<SpecialScheduleResponseDTO> decide(
            @PathVariable Long id,
            @RequestBody SpecialScheduleDecisionDTO dto) {

        return ResponseEntity.ok(
                specialScheduleService.decide(id, dto.getAction())
        );
    }

    // =====================================================
    // BULK DECIDE – APPROVER
    // =====================================================
    @Operation(
        summary = "Duyệt / từ chối nhiều lịch đặc thù",
        description = """
            Role:
            - Người được thiết lập là approver

            Ghi chú:
            - Mỗi lịch được kiểm tra quyền độc lập
            """
    )
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
    
	 // =====================================================
	 // APPROVER – PENDING LIST (NEW – APPROVAL CONFIG)
	 // =====================================================
	 @Operation(
	     summary = "Người duyệt xem lịch đặc thù cần xử lý",
	     description = """
	         Áp dụng ApprovalConfig (cá nhân + phòng ban).
	
	         - Chỉ lấy lịch PENDING
	         - Có phân trang
	         """
	 )
	 @GetMapping("/pending")
	 public ResponseEntity<Page<SpecialScheduleResponseDTO>> pendingForApprover(
	         @RequestParam(defaultValue = "0") int page,
	         @RequestParam(defaultValue = "10") int size
	 ) {
	     return ResponseEntity.ok(
	             specialScheduleService.getPendingForApprover(page, size)
	     );
	 }

}
