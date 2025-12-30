package com.tlu.hrm.service.dashboard;

import java.time.LocalTime;
import java.time.YearMonth;

import com.tlu.hrm.dto.dashboard.AttendanceStatsDTO;
import com.tlu.hrm.dto.dashboard.DashboardOverviewDTO;
import com.tlu.hrm.dto.dashboard.OtStatsDTO;
import com.tlu.hrm.dto.dashboard.SalaryStatsDTO;
import com.tlu.hrm.repository.AttendanceRecordRepository;
import com.tlu.hrm.repository.SalaryRecordRepository;

public class DashboardServiceImpl implements DashboardService {

	private final AttendanceRecordRepository attendanceRecordRepo;
    private final SalaryRecordRepository salaryRecordRepo;
    
	public DashboardServiceImpl(AttendanceRecordRepository attendanceRecordRepo,
			SalaryRecordRepository salaryRecordRepo) {
		super();
		this.attendanceRecordRepo = attendanceRecordRepo;
		this.salaryRecordRepo = salaryRecordRepo;
	}
    
	@Override
    public DashboardOverviewDTO getOverview(YearMonth month) {

        String monthStr = month.toString();

        Integer workingDays = attendanceRecordRepo.countWorkingDays(monthStr);
        Integer lateCount = attendanceRecordRepo.countLate(monthStr, LocalTime.of(8, 0));
        Integer lateEmployees = attendanceRecordRepo.countLateEmployees(monthStr, LocalTime.of(8, 0));

        Double lateRate =
                (workingDays == null || workingDays == 0)
                        ? 0.0
                        : lateCount * 100.0 / workingDays;

        AttendanceStatsDTO attendance =
                new AttendanceStatsDTO(
                        workingDays,
                        lateCount,
                        lateEmployees,
                        lateRate
                );

        Integer otMinutes = attendanceRecordRepo.sumOtMinutes(monthStr);

        OtStatsDTO ot =
                new OtStatsDTO(
                        otMinutes,
                        otMinutes / 60.0,
                        attendanceRecordRepo.countOtEmployees(monthStr)
                );

        int m = month.getMonthValue();
        int y = month.getYear();

        SalaryStatsDTO salary =
                new SalaryStatsDTO(
                		salaryRecordRepo.sumBaseSalary(m, y),
                		salaryRecordRepo.sumOtSalary(m, y),
                		salaryRecordRepo.avgSalary(m, y)
                );

        return new DashboardOverviewDTO(
                monthStr,
                attendance,
                ot,
                salary
        );
    }
}
