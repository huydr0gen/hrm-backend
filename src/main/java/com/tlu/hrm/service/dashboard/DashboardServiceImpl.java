package com.tlu.hrm.service.dashboard;

import java.time.LocalTime;
import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.dashboard.AttendanceStatsDTO;
import com.tlu.hrm.dto.dashboard.DashboardOverviewDTO;
import com.tlu.hrm.dto.dashboard.OtStatsDTO;
import com.tlu.hrm.dto.dashboard.SalaryStatsDTO;
import com.tlu.hrm.enums.EmployeeStatus;
import com.tlu.hrm.repository.AttendanceRecordRepository;
import com.tlu.hrm.repository.DepartmentRepository;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.SalaryRecordRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

	private final AttendanceRecordRepository attendanceRecordRepo;
    private final SalaryRecordRepository salaryRecordRepo;
    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;
    
	public DashboardServiceImpl(AttendanceRecordRepository attendanceRecordRepo,
			SalaryRecordRepository salaryRecordRepo, EmployeeRepository employeeRepo,
			DepartmentRepository departmentRepo) {
		super();
		this.attendanceRecordRepo = attendanceRecordRepo;
		this.salaryRecordRepo = salaryRecordRepo;
		this.employeeRepo = employeeRepo;
		this.departmentRepo = departmentRepo;
	}

	@Override
    public DashboardOverviewDTO getOverview(YearMonth month) {

        String monthStr = month.toString();
        
        // ===== Employee stats =====
        long active = employeeRepo.countByStatus(EmployeeStatus.ACTIVE);

        long inactive = employeeRepo.countByStatus(EmployeeStatus.INACTIVE);

        long locked = employeeRepo.countByStatus(EmployeeStatus.LOCKED);

        long totalEmployees = active + inactive + locked;

        // ===== Attendance =====
        Integer workingDays =
                attendanceRecordRepo.countWorkingDays(monthStr);

        Integer lateCount =
                attendanceRecordRepo.countLate(
                        monthStr,
                        LocalTime.of(8, 0)
                );

        Integer lateEmployees =
                attendanceRecordRepo.countLateEmployees(
                        monthStr,
                        LocalTime.of(8, 0)
                );
        
        double lateRate =
        	    (totalEmployees == 0)
        	        ? 0.0
        	        : lateEmployees * 100.0 / totalEmployees;

        AttendanceStatsDTO attendance =
                new AttendanceStatsDTO(
                        workingDays,
                        lateCount,
                        lateEmployees,
                        lateRate
                );

        // ===== OT =====
        Integer otMinutes =
                attendanceRecordRepo.sumOtMinutes(monthStr);

        OtStatsDTO overtime =
                new OtStatsDTO(
                        otMinutes,
                        otMinutes == null ? 0.0 : otMinutes / 60.0,
                        attendanceRecordRepo.countOtEmployees(monthStr)
                );

        // ===== Salary =====
        int m = month.getMonthValue();
        int y = month.getYear();

        SalaryStatsDTO salary =
                new SalaryStatsDTO(
                        salaryRecordRepo.sumTotalSalary(m, y),
                        salaryRecordRepo.sumOtPay(m, y),
                        salaryRecordRepo.avgTotalSalary(m, y)
                );

        // ===== Department stats =====
        long totalDepartments = departmentRepo.count();
        long activeDepartments = departmentRepo.countByActiveTrue();
        long inactiveDepartments = departmentRepo.countByActiveFalse();

        return new DashboardOverviewDTO(
        		monthStr,
                totalEmployees,
                active,
                inactive,
                locked,
                totalDepartments,
                activeDepartments,
                inactiveDepartments,
                attendance,
                overtime,
                salary
        );
    }
}
