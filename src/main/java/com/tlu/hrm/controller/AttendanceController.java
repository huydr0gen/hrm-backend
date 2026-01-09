package com.tlu.hrm.controller;

import java.time.YearMonth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tlu.hrm.dto.AttendanceImportResultDTO;
import com.tlu.hrm.dto.AttendanceMonthlyResponseDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.security.CustomUserDetails;
import com.tlu.hrm.service.AttendanceExportService;
import com.tlu.hrm.service.AttendanceImportService;
import com.tlu.hrm.service.AttendanceQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/attendance")
@Tag(
    name = "Attendance",
    description = "Import / Export chấm công theo tháng"
)
public class AttendanceController {

	private final AttendanceImportService importService;
    private final AttendanceExportService exportService;
    private final EmployeeRepository employeeRepository;
    private final AttendanceQueryService attendanceQueryService;
    
	public AttendanceController(AttendanceImportService importService, AttendanceExportService exportService,
			EmployeeRepository employeeRepository, AttendanceQueryService attendanceQueryService) {
		super();
		this.importService = importService;
		this.exportService = exportService;
		this.employeeRepository = employeeRepository;
		this.attendanceQueryService = attendanceQueryService;
	}
    
	// =====================================================
    // IMPORT ATTENDANCE (HR)
    // =====================================================
	@Operation(
        summary = "Import file chấm công theo tháng",
        description = """
            Cho phép HR upload file Excel chấm công theo tháng.

            - File được sinh ra từ chức năng export của hệ thống
            - Mỗi dòng tương ứng 1 nhân viên - 1 ngày
            - Hệ thống sẽ cập nhật dữ liệu theo (employee, workDate)
            - Không ảnh hưởng các ngày không có trong file
            """
    )
    @PostMapping("/import")
	@PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<AttendanceImportResultDTO> importAttendance(
    		@Parameter(
                description = "File Excel chấm công theo tháng",
                required = true
            )
            @RequestParam MultipartFile file,
            
            @Parameter(
                description = "Tháng chấm công (yyyy-MM). Nếu không truyền, mặc định là tháng hiện tại",
                example = "2025-09"
            )
            @RequestParam String month) {

		YearMonth yearMonth = YearMonth.parse(month);

	    AttendanceImportResultDTO result =
	            importService.importExcel(file, yearMonth);

	    return ResponseEntity.ok(result);
    }

    // =====================================================
    // EXPORT MONTHLY ATTENDANCE (HR)
    // =====================================================
	@Operation(
        summary = "Export file chấm công theo tháng",
        description = """
            Cho phép HR tải file Excel chấm công theo tháng.

            - Chỉ export nhân viên đang ACTIVE
            - Dữ liệu được dựng lại hoàn toàn từ database
            - File phản ánh trạng thái chấm công mới nhất
            """
    )
    @GetMapping("/export")
	@PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<byte[]> exportMonthlyAttendance(
    		@Parameter(
                description = "Tháng chấm công (yyyy-MM). Nếu không truyền, mặc định là tháng hiện tại",
                example = "2025-09"
            )
            @RequestParam String month) {

        YearMonth yearMonth =
                (month == null || month.isBlank())
                        ? YearMonth.now()
                        : YearMonth.parse(month);

        byte[] file = exportService.exportMonthly(yearMonth);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=attendance_" + yearMonth + ".xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }
    
    @Operation(
        summary = "Xem ngày công theo tháng (nhân viên)",
        description = "Trả về dữ liệu hiển thị dạng p:8 / p:4 x:4"
    )
    @GetMapping("/my")
    public AttendanceMonthlyResponseDTO getMyAttendance(
            @RequestParam YearMonth month) {

        Employee emp = getCurrentEmployee();
        return attendanceQueryService.getMonthly(emp.getId(), month);
    }
    
    // ===============================
    // HELPER
    // ===============================
    private Employee getCurrentEmployee() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails ud =
                (CustomUserDetails) auth.getPrincipal();

        return employeeRepository.findByUserId(ud.getId())
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));
    }
}
