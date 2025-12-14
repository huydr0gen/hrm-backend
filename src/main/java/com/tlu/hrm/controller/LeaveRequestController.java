package com.tlu.hrm.controller;

import java.util.List;

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
    // Helper: get current user id
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

        if (principal instanceof String username) {
            try {
                return Long.parseLong(username);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cannot resolve user id");
            }
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
            @ApiResponse(responseCode = "403", description = "Không có quyền EMPLOYEE")
        })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<LeaveRequestDTO> create(
            @RequestBody LeaveRequestCreateDTO dto) {

        LeaveRequestDTO created = service.createRequest(dto);
        return ResponseEntity.ok(created);
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
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/department")
    public ResponseEntity<Page<LeaveRequestDTO>> departmentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long managerId = getCurrentUserId();
        return ResponseEntity.ok(service.getDepartmentRequests(managerId, page, size));
    }

    // =====================================================
    // HR / ADMIN – FILTER LIST
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
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping
    public ResponseEntity<Page<LeaveRequestDTO>> filter(
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.getAllFiltered(employeeName, department, status, type, page, size)
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
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // =====================================================
    // HR / ADMIN – UPDATE
    // =====================================================
    @Operation(
            summary = "HR / Admin chỉnh sửa đơn nghỉ",
            description = """
                Màn hình: Chỉnh sửa đơn nghỉ
                
                Ghi chú:
                - Chỉ HR / Admin được phép
                """
        )
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<LeaveRequestDTO> adminUpdate(
            @PathVariable Long id,
            @RequestBody LeaveRequestUpdateDTO dto) {

        Long actorId = getCurrentUserId();
        return ResponseEntity.ok(service.adminUpdate(id, dto, actorId));
    }

    // =====================================================
    // HR / ADMIN – DELETE
    // =====================================================
    @Operation(
            summary = "HR / Admin xóa đơn nghỉ",
            description = """
                Màn hình: Quản lý đơn nghỉ
                
                Ghi chú:
                - Xóa một đơn theo ID
                """
        )
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "HR / Admin xóa nhiều đơn nghỉ",
            description = """
                Màn hình: Quản lý đơn nghỉ
                
                Ghi chú:
                - Xóa hàng loạt theo danh sách ID
                """
        )
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @DeleteMapping("/admin/batch")
    public ResponseEntity<Void> deleteMany(@RequestBody List<Long> ids) {
        service.deleteMany(ids);
        return ResponseEntity.noContent().build();
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
    @PreAuthorize("hasAnyRole('MANAGER','HR')")
    @PatchMapping("/decision")
    public ResponseEntity<BulkDecisionResultDTO> decideMany(
            @RequestBody BulkDecisionDTO dto) {

        Long actorId = getCurrentUserId();
        return ResponseEntity.ok(
                service.decideMany(dto.getIds(), dto.getAction(), dto.getManagerNote(), actorId)
        );
    }
}
