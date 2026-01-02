package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.*;
import com.tlu.hrm.security.CustomUserDetails;
import com.tlu.hrm.service.LeaveRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	    name = "Leave Request",
	    description = "Quản lý đơn xin nghỉ phép (Employee / Manager / HR / Admin)"
	)
@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

	private final LeaveRequestService service;

	public LeaveRequestController(LeaveRequestService service) {
		super();
		this.service = service;
	}
	
	// =====================================================
    // Helper: get current user id (SAFE)
    // =====================================================
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails ud) {
            return ud.getId();
        }

        throw new RuntimeException("Cannot resolve user id");
    }

    // =====================================================
    // CREATE – EMPLOYEE
    // =====================================================
    @Operation(
        summary = "Nhân viên tạo đơn xin nghỉ phép",
        description = """
            Màn hình: Tạo đơn nghỉ phép (Employee)

            Luồng:
            - Nhân viên gửi thông tin nghỉ
            - Đơn ở trạng thái PENDING
            - Chờ Manager / HR duyệt
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tạo đơn thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập"),
        @ApiResponse(responseCode = "403", description = "Không có quyền EMPLOYEE")
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<LeaveRequestDTO> create(
            @RequestBody LeaveRequestCreateDTO dto) {

        return ResponseEntity.ok(service.createRequest(dto));
    }

    // =====================================================
    // EMPLOYEE – MY REQUESTS
    // =====================================================
    @Operation(
        summary = "Nhân viên xem các đơn nghỉ của mình",
        description = """
            Màn hình: Danh sách đơn nghỉ của tôi

            Ghi chú:
            - Chỉ lấy đơn của user đang đăng nhập
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy danh sách đơn nghỉ thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập"),
        @ApiResponse(responseCode = "403", description = "Không có quyền EMPLOYEE")
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<Page<LeaveRequestDTO>> myRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = getCurrentUserId();
        return ResponseEntity.ok(service.getMyRequests(userId, page, size));
    }

    // =====================================================
    // MANAGER – DEPARTMENT REQUESTS
    // =====================================================
    @Operation(
        summary = "Quản lý xem đơn nghỉ của phòng ban",
        description = """
            Màn hình: Duyệt đơn nghỉ (Manager)

            Ghi chú:
            - Chỉ hiển thị đơn của nhân viên trong phòng ban
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy danh sách đơn nghỉ phòng ban thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền MANAGER")
    })
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/department")
    public ResponseEntity<Page<LeaveRequestDTO>> departmentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long managerId = getCurrentUserId();
        return ResponseEntity.ok(service.getDepartmentRequests(managerId, page, size));
    }

    // =====================================================
    // HR / ADMIN – FILTER LIST (READ ONLY)
    // =====================================================
    @Operation(
        summary = "HR / Admin tìm kiếm và lọc đơn nghỉ",
        description = """
            Màn hình: Danh sách đơn nghỉ (HR / Admin)

            Có thể lọc theo:
            - Tên nhân viên
            - Phòng ban
            - Trạng thái đơn
            - Loại nghỉ
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy danh sách đơn nghỉ thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền HR / ADMIN")
    })
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping
    public ResponseEntity<Page<LeaveRequestDTO>> filter(
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.getAllFiltered(employeeName, departmentId, status, type, page, size)
        );
    }

    // =====================================================
    // GET BY ID
    // =====================================================
    @Operation(
        summary = "Xem chi tiết đơn nghỉ phép",
        description = """
            Màn hình: Chi tiết đơn nghỉ

            Role:
            - EMPLOYEE (đơn của mình)
            - MANAGER / HR / ADMIN
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy chi tiết đơn nghỉ thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn nghỉ")
    })
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','HR','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // =====================================================
    // EMPLOYEE – UPDATE (PENDING ONLY)
    // =====================================================
    @Operation(
        summary = "Nhân viên chỉnh sửa đơn nghỉ (chỉ khi PENDING)",
        description = """
            Màn hình: Chỉnh sửa đơn nghỉ

            Rule:
            - Chỉ EMPLOYEE
            - Chỉ sửa đơn của chính mình
            - Chỉ khi trạng thái = PENDING
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "400", description = "Đơn không còn ở trạng thái PENDING"),
        @ApiResponse(responseCode = "403", description = "Không có quyền EMPLOYEE"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn nghỉ")
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> employeeUpdate(
            @PathVariable Long id,
            @RequestBody LeaveRequestUpdateDTO dto) {

        Long userId = getCurrentUserId();
        return ResponseEntity.ok(service.employeeUpdate(id, dto, userId));
    }

    // =====================================================
    // DECIDE – MANAGER / HR
    // =====================================================
    @Operation(
        summary = "Duyệt hoặc từ chối đơn nghỉ",
        description = """
            Màn hình: Duyệt đơn nghỉ (Manager / HR)

            Action:
            - APPROVE
            - REJECT
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Xử lý đơn nghỉ thành công"),
        @ApiResponse(responseCode = "400", description = "Đơn đã được xử lý hoặc action không hợp lệ"),
        @ApiResponse(responseCode = "403", description = "Không có quyền MANAGER / HR")
    })
    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PatchMapping("/{id}/decision")
    public ResponseEntity<LeaveRequestDTO> decide(
            @PathVariable Long id,
            @RequestBody LeaveRequestDecisionDTO dto) {

        Long actorId = getCurrentUserId();
        return ResponseEntity.ok(
                service.decide(id, dto.getAction(), dto.getManagerNote(), actorId)
        );
    }

    // =====================================================
    // BULK DECIDE – MANAGER / HR
    // =====================================================
    @Operation(
        summary = "Duyệt / từ chối nhiều đơn nghỉ cùng lúc",
        description = """
            Màn hình: Duyệt hàng loạt

            Ghi chú:
            - Áp dụng cùng một action cho nhiều đơn
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Xử lý hàng loạt thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền MANAGER / HR")
    })
    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PatchMapping("/decision")
    public ResponseEntity<BulkDecisionResultDTO> decideMany(
            @RequestBody BulkDecisionDTO dto) {

        Long actorId = getCurrentUserId();
        return ResponseEntity.ok(
                service.decideMany(
                        dto.getIds(),
                        dto.getAction(),
                        dto.getManagerNote(),
                        actorId
                )
        );
    }
    
 // =====================================================
 // APPROVER – PENDING LIST (NEW)
 // =====================================================
 @Operation(
     summary = "Người duyệt xem danh sách đơn nghỉ cần duyệt",
     description = """
         Dùng cho Manager / HR được thiết lập duyệt.

         - Áp dụng ApprovalConfig (cá nhân + phòng ban)
         - Chỉ lấy đơn PENDING
         - Có phân trang
         """
	 )
	 @PreAuthorize("hasAnyRole('MANAGER','HR')")
	 @GetMapping("/pending")
	 public ResponseEntity<Page<LeaveRequestDTO>> pendingForApprover(
	         @RequestParam(defaultValue = "0") int page,
	         @RequestParam(defaultValue = "10") int size
	 ) {
	     return ResponseEntity.ok(
	             service.getPendingForApprover(page, size)
	     );
	 }
}
