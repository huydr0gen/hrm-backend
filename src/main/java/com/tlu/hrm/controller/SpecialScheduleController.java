package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.BulkDecisionDTO;
import com.tlu.hrm.dto.BulkDecisionResultDTO;
import com.tlu.hrm.dto.SpecialScheduleCreateDTO;
import com.tlu.hrm.dto.SpecialScheduleDecisionDTO;
import com.tlu.hrm.dto.SpecialScheduleFilterDTO;
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
    // LIST (EMPLOYEE / MANAGER / HR)
    // =====================================================
	@Operation(
        summary = "Tìm kiếm danh sách lịch đặc thù",
        description = """
            Role:
            - EMPLOYEE: chỉ xem lịch của chính mình
            - MANAGER: xem lịch của nhân viên trong phòng ban
            - ADMIN: KHÔNG được truy cập nghiệp vụ này

            Ghi chú nghiệp vụ:
            - API dùng POST để hỗ trợ filter linh hoạt
            - Có thể lọc theo: khoảng thời gian, trạng thái, phòng ban
            - Phân quyền được xử lý ở Service
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PostMapping("/search")
    public ResponseEntity<Page<SpecialScheduleResponseDTO>> list(
            @RequestBody SpecialScheduleFilterDTO filter) {

        return ResponseEntity.ok(
                specialScheduleService.list(filter)
        );
    }

	// =====================================================
    // DETAIL (EMPLOYEE / MANAGER / HR)
    // =====================================================
	@Operation(
        summary = "Xem chi tiết một lịch đặc thù",
        description = """
            Role:
            - EMPLOYEE: chỉ xem lịch của chính mình
            - MANAGER: xem lịch của nhân viên trong phòng ban
            - ADMIN: KHÔNG được truy cập

            Ghi chú:
            - Dùng cho màn hình xem chi tiết
            - Phân quyền kiểm soát ở Service
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy chi tiết thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy lịch")
    })
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
            - Nhân viên tạo yêu cầu lịch đặc thù (thai sản, on-site, con nhỏ, khác)
            - Lịch sau khi tạo có trạng thái PENDING
            - Người duyệt được xác định theo cấu hình duyệt (cá nhân hoặc phòng ban)
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tạo lịch đặc thù thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ / bị trùng lịch"),
        @ApiResponse(responseCode = "403", description = "Không có quyền tạo")
    })
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

            Điều kiện nghiệp vụ:
            - Chỉ được chỉnh sửa lịch của chính mình
            - Chỉ chỉnh sửa khi trạng thái là PENDING
            - Không áp dụng cho lịch đã được duyệt hoặc từ chối

            Ghi chú:
            - Đây không phải hành động duyệt
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cập nhật lịch thành công"),
        @ApiResponse(responseCode = "400", description = "Lịch không còn ở trạng thái PENDING"),
        @ApiResponse(responseCode = "403", description = "Không có quyền chỉnh sửa")
    })
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
	@Operation(
        summary = "Duyệt hoặc từ chối lịch đặc thù",
        description = """
            Role:
            - Người được thiết lập là approver của lịch (MANAGER hoặc HR tuỳ cấu hình)

            Nghiệp vụ:
            - Chỉ người có approverId trùng với user hiện tại mới được duyệt
            - Chỉ duyệt khi lịch đang ở trạng thái PENDING

            Action:
            - APPROVE: duyệt
            - REJECT: từ chối
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Xử lý duyệt thành công"),
        @ApiResponse(responseCode = "403", description = "Không phải người duyệt"),
        @ApiResponse(responseCode = "400", description = "Lịch đã được xử lý trước đó")
    })
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
	@Operation(
        summary = "Duyệt / từ chối nhiều lịch đặc thù",
        description = """
            Role:
            - Người được thiết lập là approver của từng lịch

            Nghiệp vụ:
            - Áp dụng cùng một action cho nhiều lịch
            - Mỗi lịch được kiểm tra quyền độc lập
            - Kết quả trả về gồm danh sách thành công và thất bại
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Xử lý hàng loạt hoàn tất")
    })
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
