package com.tlu.hrm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tlu.hrm.dto.DepartmentCreateDTO;
import com.tlu.hrm.dto.DepartmentDTO;
import com.tlu.hrm.dto.DepartmentUpdateDTO;
import com.tlu.hrm.service.DepartmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	    name = "Department",
	    description = "Quản lý phòng ban (master data nhân sự)"
	)
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

	private final DepartmentService departmentService;

	public DepartmentController(DepartmentService departmentService) {
		super();
		this.departmentService = departmentService;
	}
	
	// ================= CREATE =================

    @Operation(
        summary = "Tạo mới phòng ban",
        description = """
            Màn hình: Quản lý phòng ban
            
            Role:
            - HR
            
            Ghi chú:
            - Tên phòng ban không được trùng
            - Phòng ban tạo mới mặc định active = true
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tạo phòng ban thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền HR")
    })
    @PreAuthorize("hasRole('HR')")
    @PostMapping
    public ResponseEntity<DepartmentDTO> create(
            @RequestBody DepartmentCreateDTO dto) {

        return ResponseEntity.ok(departmentService.create(dto));
    }

    // ================= UPDATE =================

    @Operation(
        summary = "Cập nhật phòng ban",
        description = """
            Màn hình: Chỉnh sửa phòng ban
            
            Role:
            - HR
            
            Ghi chú:
            - Cập nhật tên và trạng thái hoạt động phòng ban
            - Không cho phép trùng tên
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cập nhật phòng ban thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền HR"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy phòng ban")
    })
    @PreAuthorize("hasRole('HR')")
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> update(
            @PathVariable Long id,
            @RequestBody DepartmentUpdateDTO dto) {

        return ResponseEntity.ok(departmentService.update(id, dto));
    }

    // ================= DELETE (SOFT) =================

    @Operation(
        summary = "Ngừng hoạt động phòng ban",
        description = """
            Màn hình: Quản lý phòng ban
            
            Role:
            - HR
            
            Ghi chú:
            - Không xóa cứng dữ liệu
            - Chỉ chuyển trạng thái active = false
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ngừng hoạt động phòng ban thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền HR"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy phòng ban")
    })
    @PreAuthorize("hasRole('HR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ================= GET ALL ACTIVE =================

    @Operation(
        summary = "Lấy danh sách phòng ban đang hoạt động",
        description = """
            Màn hình:
            - Dropdown chọn phòng ban (tạo / sửa nhân viên)
            - Danh sách phòng ban
            
            Role:
            - HR
            - ADMIN (nếu cần xem)
            
            Ghi chú:
            - Chỉ trả về phòng ban active = true
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lấy danh sách phòng ban thành công")
    })
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/active")
    public ResponseEntity<List<DepartmentDTO>> getAllActive() {

        return ResponseEntity.ok(departmentService.getAllActive());
    }
}
