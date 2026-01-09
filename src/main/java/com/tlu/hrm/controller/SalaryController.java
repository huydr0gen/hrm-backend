package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.MySalaryListItemDTO;
import com.tlu.hrm.dto.MySalaryResponseDTO;
import com.tlu.hrm.repository.SalaryRecordRepository;
import com.tlu.hrm.service.SalaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/salaries")
@PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
@Tag(name = "Salary", description = "API quản lý và tra cứu lương của nhân viên")
public class SalaryController {

	private final SalaryService salaryService;
	private final SalaryRecordRepository salaryRecordRepository;

	public SalaryController(SalaryService salaryService, SalaryRecordRepository salaryRecordRepository) {
		super();
		this.salaryService = salaryService;
		this.salaryRecordRepository = salaryRecordRepository;
	}
	
	@GetMapping("/my")
	@Operation(
        summary = "Xem chi tiết lương của tôi theo tháng",
        description = "API cho phép nhân viên xem chi tiết lương của mình theo tháng và năm cụ thể"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy thông tin lương thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy dữ liệu lương")
    })
    public MySalaryResponseDTO getMySalary(
        @RequestParam int month,
        @RequestParam int year) {

        return salaryService.getMySalary(month, year);
    }
	
	@GetMapping("/my/list")
	@Operation(
        summary = "Danh sách các kỳ lương của tôi",
        description = "API trả về danh sách các kỳ lương của nhân viên đang đăng nhập, có hỗ trợ phân trang, sắp xếp và lọc theo tháng/năm"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách lương thành công")
    })
	public Page<MySalaryListItemDTO> getMySalaryList(
	        @RequestParam(required = false) Integer month,
	        @RequestParam(required = false) Integer year,

	        @PageableDefault(
	            sort = {"year", "month"},
	            direction = Sort.Direction.DESC
	        )
	        Pageable pageable
	) {
	    return salaryService.getMySalaryList(month, year, pageable);
	}
	
	@DeleteMapping
	@PreAuthorize("hasAuthority('ROLE_HR')")
	@Operation(
        summary = "Xóa dữ liệu lương theo tháng",
        description = "API cho phép HR xóa toàn bộ dữ liệu lương của một tháng cụ thể"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xóa thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
	public void deleteSalaryByMonth(
	    @RequestParam int month,
	    @RequestParam int year
	) {
		salaryRecordRepository.deleteByMonthAndYear(month, year);
	}
}
