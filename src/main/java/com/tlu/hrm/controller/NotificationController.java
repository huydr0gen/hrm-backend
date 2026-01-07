package com.tlu.hrm.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.NotificationResponse;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/notifications")
@Tag(
    name = "Notification",
    description = "API quản lý thông báo (icon chuông, realtime, đọc/chưa đọc)"
)
public class NotificationController {

	private final NotificationService notificationService;
    private final EmployeeRepository employeeRepository;

    public NotificationController(
            NotificationService notificationService,
            EmployeeRepository employeeRepository
    ) {
        this.notificationService = notificationService;
        this.employeeRepository = employeeRepository;
    }

    /* ================== HELPER ================== */

    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        return employeeRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username)
                )
                .getId();
    }

    /* ================== API ================== */

    @Operation(
        summary = "Lấy danh sách thông báo của người dùng hiện tại",
        description = "API dùng khi người dùng click vào icon chuông để xem danh sách thông báo. " +
                      "Danh sách được sắp xếp theo thời gian giảm dần."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thông báo thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ")
    })
    @GetMapping
    public List<NotificationResponse> getMyNotifications() {
        Long userId = getCurrentUserId();
        return notificationService.getNotificationsByReceiver(userId);
    }

    // Badge số chưa đọc
    @Operation(
        summary = "Lấy số lượng thông báo chưa đọc",
        description = "API dùng để hiển thị badge số lượng thông báo chưa đọc trên icon chuông."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy số lượng thông báo chưa đọc thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ")
    })
    @GetMapping("/unread-count")
    public long countUnread() {
        Long userId = getCurrentUserId();
        return notificationService.countUnread(userId);
    }

    @Operation(
        summary = "Đánh dấu một thông báo đã đọc",
        description = "API được gọi khi người dùng click vào một thông báo cụ thể. " +
                      "Chỉ cho phép đánh dấu các thông báo thuộc về người dùng hiện tại."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Đánh dấu thông báo đã đọc thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy thông báo")
    })
    @PostMapping("/{id}/read")
    public void markOneAsRead(@PathVariable("id") Long notificationId) {
        Long userId = getCurrentUserId();
        notificationService.markAsRead(notificationId, userId);
    }
    
    // Đánh dấu tất cả đã đọc
    @Operation(
        summary = "Đánh dấu tất cả thông báo đã đọc",
        description = "API cho phép người dùng đánh dấu toàn bộ thông báo của mình là đã đọc."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Đánh dấu tất cả thông báo đã đọc thành công"),
        @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ")
    })
    @PostMapping("/mark-all-read")
    public void markAllAsRead() {
        Long userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
    }
}
