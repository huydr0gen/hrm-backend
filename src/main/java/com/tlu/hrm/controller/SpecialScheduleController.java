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
	    description = "Quản lý lịch làm việc đặc thù (ngày lễ, làm bù, ca đặc biệt)"
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
	            - EMPLOYEE: chỉ xem lịch của mình
	            - MANAGER: xem lịch nhân viên trong phòng ban
	            - HR: xem toàn bộ lịch
	            
	            Ghi chú:
	            - Dùng POST để filter linh hoạt
	            - Có thể lọc theo thời gian, trạng thái, phòng ban
	            - ADMIN không được truy cập nghiệp vụ này
	            """
	    )
	@ApiResponses({
	        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
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
	        summary = "Xem chi tiết lịch đặc thù",
	        description = """
	            Màn hình: Chi tiết lịch đặc thù
	            Role:
	            - EMPLOYEE: xem lịch của mình
	            - MANAGER: xem lịch trong phòng ban
	            - HR: xem toàn bộ
	            
	            ADMIN không được truy cập
	            
	            Ghi chú:
	            - Dùng để xem thông tin chi tiết 1 lịch
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
	            Màn hình: Đăng ký lịch đặc thù
	            
	            Ví dụ:
	            - Làm bù ngày lễ
	            - Đăng ký ca đặc biệt
	            
	            Luồng:
	            - Nhân viên gửi yêu cầu
	            - Lịch ở trạng thái chờ duyệt
	            """
	    )
	    @ApiResponses({
	        @ApiResponse(responseCode = "200", description = "Tạo lịch đặc thù thành công")
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
	            Màn hình: Chỉnh sửa lịch đặc thù
	            Role:
            - EMPLOYEE

            Điều kiện:
            - Chỉ chỉnh sửa lịch của chính mình
            - Chỉ khi trạng thái là PENDING
            
	            Ghi chú:
	            - Dùng khi cần điều chỉnh thông tin
	            - Không phải hành động duyệt
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
    // DECIDE – HR / MANAGER
    // =====================================================
	@Operation(
	        summary = "Duyệt hoặc từ chối lịch đặc thù",
	        description = """
	            Màn hình: Duyệt lịch đặc thù (HR / Manager)
	            
	            Role:
	            - HR: duyệt tất cả
	            - MANAGER: chỉ duyệt lịch của nhân viên trong phòng ban
	            
	            ADMIN và EMPLOYEE không được phép
	            
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
    // BULK DECIDE – HR / MANAGER
    // =====================================================
	@Operation(
	        summary = "Duyệt / từ chối nhiều lịch đặc thù",
	        description = """
	            Màn hình: Duyệt lịch đặc thù hàng loạt
	            
	            Role:
	            - HR
	            - MANAGER (chỉ trong phòng ban)
	            
	            Ghi chú:
	            - Áp dụng cùng một action cho nhiều bản ghi
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
}
