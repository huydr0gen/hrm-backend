package com.tlu.hrm.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlu.hrm.entities.AttendanceRecord;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.enums.EmployeeStatus;
import com.tlu.hrm.repository.AttendanceRecordRepository;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.utils.ExcelAttendanceMonthlyExporter;

@Service
@Transactional(readOnly = true)
public class AttendanceExportServiceImpl implements AttendanceExportService {

	private final EmployeeRepository employeeRepo;
    private final AttendanceRecordRepository attendanceRepo;
    
	public AttendanceExportServiceImpl(EmployeeRepository employeeRepo, AttendanceRecordRepository attendanceRepo) {
		super();
		this.employeeRepo = employeeRepo;
		this.attendanceRepo = attendanceRepo;
	}
    
	@Override
    public byte[] exportMonthly(YearMonth month) {
		// Lấy toàn bộ nhân viên ACTIVE
        List<Employee> employees =
                employeeRepo.findAllByStatus(EmployeeStatus.ACTIVE);

        // Xác định khoảng thời gian trong tháng
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        // Lấy toàn bộ attendance records trong tháng
        List<AttendanceRecord> records =
                attendanceRepo.findByWorkDateBetween(startDate, endDate);

        // Map dữ liệu để tra nhanh: employeeId -> (date -> record)
        Map<Long, Map<LocalDate, AttendanceRecord>> attendanceMap = new HashMap<>();

        for (AttendanceRecord record : records) {
            attendanceMap
                .computeIfAbsent(
                    record.getEmployee().getId(),
                    k -> new HashMap<>()
                )
                .put(record.getWorkDate(), record);
        }

        // Gọi util để sinh file Excel
        return ExcelAttendanceMonthlyExporter.generate(
                employees,
                attendanceMap,
                month
        );
    }
}
