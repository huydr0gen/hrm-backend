package com.tlu.hrm.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.SalaryImportHistoryResponseDTO;
import com.tlu.hrm.service.SalaryImportHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/salary/import-histories")
@Tag(name = "Salary Import History", description = "Quản lý lịch sử import lương")
public class SalaryImportHistoryController {

	private final SalaryImportHistoryService service;

	public SalaryImportHistoryController(SalaryImportHistoryService service) {
		super();
		this.service = service;
	}
	
	//Get all
	@Operation(
        summary = "Lấy danh sách toàn bộ lịch sử import lương",
        description = """
            Trả về danh sách tất cả các lần import lương.
            
            - Có phân trang
            - Sắp xếp theo thời gian tạo mới nhất trước
            - Chỉ HR được phép truy cập
        """
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<Page<SalaryImportHistoryResponseDTO>> getAll(
    		@Parameter(description = "Trang hiện tại (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số bản ghi mỗi trang", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getAll(page, size));
    }

	
    //Filter by month
	@Operation(
        summary = "Lọc lịch sử import lương theo tháng",
        description = """
            Trả về danh sách các lần import lương theo tháng.
            
            Định dạng month: YYYY-MM (ví dụ: 2025-11)
            
            - Có phân trang
            - Sắp xếp theo thời gian tạo mới nhất trước
            - Chỉ HR được phép truy cập
        """
    )
    @GetMapping("/by-month")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<Page<SalaryImportHistoryResponseDTO>> getByMonth(
    		@Parameter(description = "Tháng cần lọc, định dạng YYYY-MM", example = "2025-11", required = true)
            @RequestParam String month,

            @Parameter(description = "Trang hiện tại (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số bản ghi mỗi trang", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getByMonth(month, page, size));
    }
}
