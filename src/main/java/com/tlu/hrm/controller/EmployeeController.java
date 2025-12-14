package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tlu.hrm.dto.EmployeeCreateDTO;
import com.tlu.hrm.dto.EmployeeDTO;
import com.tlu.hrm.dto.EmployeeUpdateDTO;
import com.tlu.hrm.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
	    name = "Employee",
	    description = "Quản lý thông tin nhân viên (hồ sơ nhân sự)"
	)
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		super();
		this.employeeService = employeeService;
	}

	// CREATE EMPLOYEE -----------------------------------------------------------

    @Operation(
        summary = "Tạo mới nhân viên",
        description = """
            Màn hình: Quản lý nhân sự
            
            Luồng nghiệp vụ:
            - HR / ADMIN tạo hồ sơ nhân viên
            - Nhân viên chưa có tài khoản đăng nhập
            - Có thể tạo user sau từ employee
            
            Ghi chú:
            - Đây là bước đầu tiên trong quy trình nhân sự
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tạo nhân viên thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền HR / ADMIN")
    })
	@PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ResponseEntity<EmployeeDTO> create(@RequestBody EmployeeCreateDTO dto) {
        return ResponseEntity.ok(employeeService.createEmployee(dto));
    }

    
 // GET ALL EMPLOYEES ----------------------------------------------------------

    @Operation(
        summary = "Lấy danh sách nhân viên (phân trang)",
        description = """
            Màn hình: Danh sách nhân viên
            
            Role:
            - ADMIN
            - HR
            
            Ghi chú cho FE:
            - Dùng để hiển thị bảng nhân sự
            """
    )
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<Page<EmployeeDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(employeeService.getAllEmployees(page, size));
    }

 // GET EMPLOYEES WITHOUT USER -------------------------------------------------

    @Operation(
        summary = "Lấy danh sách nhân viên chưa có tài khoản",
        description = """
            Màn hình: Tạo user từ employee
            
            Luồng:
            - FE gọi API này để lấy danh sách employee
            - Chỉ hiển thị những employee chưa có user
            
            Ghi chú:
            - Dùng cho chức năng 'Tạo tài khoản cho nhân viên'
            """
    )
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/no-user")
    public ResponseEntity<Page<EmployeeDTO>> getWithoutUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(employeeService.getEmployeesWithoutUser(page, size));
    }

 // GET EMPLOYEE BY ID ---------------------------------------------------------

    @Operation(
        summary = "Lấy chi tiết nhân viên",
        description = """
            Màn hình: Chi tiết nhân viên
            
            Role:
            - ADMIN
            - HR
            - MANAGER
            """
    )
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

 // UPDATE EMPLOYEE ------------------------------------------------------------

    @Operation(
        summary = "Cập nhật thông tin nhân viên",
        description = """
            Màn hình: Chỉnh sửa hồ sơ nhân viên
            
            Role:
            - ADMIN
            - HR
            """
    )
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> update(
            @PathVariable Long id,
            @RequestBody EmployeeUpdateDTO dto) {

        return ResponseEntity.ok(employeeService.updateEmployee(id, dto));
    }

 // DELETE EMPLOYEE ------------------------------------------------------------

    @Operation(
        summary = "Xóa nhân viên",
        description = """
            Màn hình: Quản lý nhân sự
            
            Ghi chú:
            - Chỉ ADMIN được phép xóa
            - Thường dùng khi nhập sai dữ liệu
            """
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
