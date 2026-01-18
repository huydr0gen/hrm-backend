package com.tlu.hrm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.EmployeeCertificateCreateDTO;
import com.tlu.hrm.dto.EmployeeCertificateUpdateDTO;
import com.tlu.hrm.enums.CertificateStatus;
import com.tlu.hrm.security.CustomUserDetails;
import com.tlu.hrm.service.EmployeeCertificateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/certificates")
@Tag(name = "Employee Certificates", description = "APIs quản lý bằng cấp/chứng chỉ của nhân viên")
public class EmployeeCertificateController {

    private final EmployeeCertificateService service;

    public EmployeeCertificateController(EmployeeCertificateService service) {
        this.service = service;
    }

    // =====================================================
    // HR
    // =====================================================

    @Operation(summary = "Danh sách tất cả bằng cấp (HR)",
               description = "Lấy danh sách tất cả bằng cấp trong hệ thống, hỗ trợ phân trang và sắp xếp")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> listAll(
            @Parameter(description = "Trang hiện tại (bắt đầu từ 0)") 
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số phần tử mỗi trang") 
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sắp xếp, ví dụ: createdAt,desc") 
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        return ResponseEntity.ok(service.listAll(page, size, sort));
    }

    @Operation(summary = "Lọc bằng cấp theo trạng thái (HR)",
               description = "Lọc danh sách bằng cấp theo trạng thái: ACTIVE hoặc EXPIRED")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lọc thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> filterByStatus(
            @Parameter(description = "Trạng thái bằng cấp: ACTIVE hoặc EXPIRED", example = "ACTIVE")
            @RequestParam CertificateStatus status,

            @Parameter(description = "Trang hiện tại")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số phần tử mỗi trang")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sắp xếp, ví dụ: createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        return ResponseEntity.ok(
                service.listByStatus(status, page, size, sort)
        );
    }

    @Operation(summary = "Tìm kiếm bằng cấp theo nhân viên (HR)",
               description = "Tìm kiếm theo mã nhân viên hoặc tên nhân viên")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tìm kiếm thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> search(
            @Parameter(description = "Từ khóa tìm kiếm (mã NV hoặc tên NV)") 
            @RequestParam String keyword,

            @Parameter(description = "Trang hiện tại") 
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số phần tử mỗi trang") 
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sắp xếp, ví dụ: createdAt,desc") 
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        return ResponseEntity.ok(service.search(keyword, page, size, sort));
    }

    @Operation(summary = "Tạo mới bằng cấp cho nhân viên (HR)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tạo thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> create(
            @RequestBody EmployeeCertificateCreateDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary = "Cập nhật bằng cấp (HR)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> update(
            @Parameter(description = "ID của bằng cấp") 
            @PathVariable Long id,

            @RequestBody EmployeeCertificateUpdateDTO dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Xóa bằng cấp (HR)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Xóa thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID của bằng cấp") 
            @PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Xem chi tiết bằng cấp (HR)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy chi tiết thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<?> getDetail(
            @Parameter(description = "ID của bằng cấp") 
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getDetail(id));
    }

    // =====================================================
    // EMPLOYEE
    // =====================================================

    @Operation(summary = "Danh sách bằng cấp của tôi (Employee)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<?> getMyCertificates(
            @Parameter(description = "Trang hiện tại") 
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số phần tử mỗi trang") 
            @RequestParam(defaultValue = "10") int size) {

        CustomUserDetails user =
                (CustomUserDetails) SecurityContextHolder
                        .getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(
                service.getMyCertificates(user.getId(), page, size)
        );
    }

    @Operation(summary = "Xem chi tiết bằng cấp của tôi (Employee)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy chi tiết thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @GetMapping("/my/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<?> getMyCertificateDetail(
            @Parameter(description = "ID của bằng cấp") 
            @PathVariable Long id) {

        CustomUserDetails user =
                (CustomUserDetails) SecurityContextHolder
                        .getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(
                service.getMyCertificateDetail(user.getId(), id)
        );
    }
}
