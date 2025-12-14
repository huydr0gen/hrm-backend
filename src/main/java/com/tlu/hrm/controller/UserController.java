package com.tlu.hrm.controller;

import com.tlu.hrm.dto.UserCreateDTO;
import com.tlu.hrm.dto.UserDTO;
import com.tlu.hrm.dto.UserUpdateDTO;
import com.tlu.hrm.entities.User;
import com.tlu.hrm.service.UserService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
		name = "User",
		description = "Quản lý tài khoản người dùng trong hệ thống (ADMIN / HR)"
	    )
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
		super();
		this.userService = userService;
	}
    
 // CREATE USER
    @Operation(
            summary = "Tạo user mới",
            description = """
                Màn hình: Quản lý người dùng (Admin)
                
                Luồng:
                - ADMIN tạo user mới thủ công
                - Gán role ngay khi tạo
                
                Ghi chú:
                - Dùng khi user chưa gắn với employee
                """
            )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo user thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO dto) {
        return ResponseEntity.ok(mapToDto(userService.createUser(dto)));
    }

 // CREATE USER FROM EMPLOYEE --------------------------------------------------

    @Operation(
        summary = "Tạo user từ employee có sẵn",
        description = """
            Màn hình: Quản lý nhân sự
            
            Luồng:
            - HR / ADMIN tạo employee trước
            - Sau đó tạo user gắn với employee
            
            Ghi chú cho FE:
            - Không cần gửi username / password
            - Backend tự sinh thông tin đăng nhập
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tạo user từ employee thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/from-employee/{employeeId}")
    public ResponseEntity<UserDTO> createUserFromEmployee(@PathVariable Long employeeId) {
        User user = userService.createUserFromEmployee(employeeId);
        return ResponseEntity.ok(mapToDto(user));
    }
    
 // RESET PASSWORD ------------------------------------------------------------

    @Operation(
        summary = "Reset mật khẩu user",
        description = """
            Màn hình: Quản lý người dùng
            
            Luồng:
            - ADMIN reset mật khẩu user
            - Mật khẩu mới được sinh tự động
            
            Ghi chú:
            - FE không cần gửi body
            """
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<UserDTO> resetPassword(@PathVariable Long id) {
        User user = userService.resetPassword(id);
        return ResponseEntity.ok(mapToDto(user));
    }

    // GET USERS WITH PAGINATION
    @Operation(
            summary = "Lấy danh sách user (phân trang)",
            description = """
                Màn hình: Danh sách người dùng
                
                Role:
                - ADMIN
                - HR
                
                Ghi chú:
                - Dùng để hiển thị bảng user
                """
            )
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<User> users = userService.getUsers(page, size);

        Page<UserDTO> dtoPage = users.map(this::mapToDto);

        return ResponseEntity.ok(dtoPage);
    }

    // GET USER BY ID
    @Operation(
            summary = "Lấy thông tin user theo ID",
            description = """
                Màn hình: Chi tiết người dùng
                
                Role:
                - ADMIN
                - HR
                - MANAGER
                - EMPLOYEE
                """
        )
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER','EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(mapToDto(user));
    }

    // UPDATE USER
    @Operation(
            summary = "Cập nhật thông tin user",
            description = """
                Màn hình: Chỉnh sửa người dùng
                
                Role:
                - ADMIN
                - HR
                """
        )
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        User updateUser = userService.updateUser(id, dto);
        return ResponseEntity.ok(mapToDto(updateUser));
    }

    // DELETE USER
    @Operation(
            summary = "Xóa user",
            description = """
                Màn hình: Quản lý người dùng
                
                Ghi chú:
                - Chỉ ADMIN được phép xóa
                """
        )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

 // ASSIGN ROLES --------------------------------------------------------------

    @Operation(
        summary = "Gán role cho user",
        description = """
            Màn hình: Phân quyền người dùng
            
            FE gửi:
            - Danh sách tên role (String)
            
            Ví dụ:
            ["ADMIN", "HR"]
            """
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles")
    public ResponseEntity<UserDTO> assignRoles(@PathVariable Long id, @RequestBody Set<String> roleNames) {
        User user = userService.assignRoles(id, roleNames);
        return ResponseEntity.ok(mapToDto(user));
    }

 // ACTIVATE / DEACTIVATE / LOCK ----------------------------------------------

    @Operation(summary = "Kích hoạt user")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Vô hiệu hóa user")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Khóa user")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/lock")
    public ResponseEntity<Void> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return ResponseEntity.ok().build();
    }

    // GET CURRENT LOGGED-IN USER
    @Operation(
            summary = "Lấy thông tin user đang đăng nhập",
            description = """
                Màn hình: Profile cá nhân
                
                Ghi chú:
                - Dùng accessToken hiện tại
                - Không cần truyền ID
                """
        )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(mapToDto(user));
    }

    private UserDTO mapToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());

        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet()));

        dto.setStatus(user.getStatus());

        if (user.getEmployee() != null) {
            dto.setEmployeeId(user.getEmployee().getId());
        }

        dto.setLastLogin(user.getLastLogin());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }
}
