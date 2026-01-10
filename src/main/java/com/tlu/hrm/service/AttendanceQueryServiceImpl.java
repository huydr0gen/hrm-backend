package com.tlu.hrm.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.AttendanceDayResponseDTO;
import com.tlu.hrm.dto.AttendanceMonthlyResponseDTO;
import com.tlu.hrm.entities.AttendanceRecord;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.repository.AttendanceRecordRepository;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.utils.AttendanceDisplayUtil;

@Service
public class AttendanceQueryServiceImpl implements AttendanceQueryService {

	private final AttendanceRecordRepository attendanceRepo;
	private final EmployeeRepository employeeRepo;

	public AttendanceQueryServiceImpl(AttendanceRecordRepository attendanceRepo, EmployeeRepository employeeRepo) {
		super();
		this.attendanceRepo = attendanceRepo;
		this.employeeRepo = employeeRepo;
	}
	
	@Override
    public AttendanceMonthlyResponseDTO getMonthly(Long employeeId, YearMonth month) {

		Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate onboardDate = employee.getOnboardDate();
        LocalDate startOfMonth = month.atDay(1);
        LocalDate endOfMonth = month.atEndOfMonth();

        // Nếu chưa onboard trong tháng này
        if (onboardDate.isAfter(endOfMonth)) {
            AttendanceMonthlyResponseDTO empty = new AttendanceMonthlyResponseDTO();
            empty.setDays(new ArrayList<>());
            empty.setTotalPaidMinutes(0);
            empty.setTotalWorkingDays(0);
            empty.setTotalOTMinutes(0);
            empty.setTotalOTHours(0);
            return empty;
        }

        LocalDate actualStart = onboardDate.isAfter(startOfMonth)
                ? onboardDate
                : startOfMonth;

        List<AttendanceRecord> records =
                attendanceRepo.findMonthly(employeeId, actualStart, endOfMonth);

        int totalOTMinutes = records.stream()
                .map(AttendanceRecord::getOtMinutes)
                .filter(m -> m != null)
                .mapToInt(Integer::intValue)
                .sum();

        Map<LocalDate, AttendanceRecord> map =
                records.stream()
                        .collect(Collectors.toMap(
                                AttendanceRecord::getWorkDate,
                                r -> r
                        ));

        List<AttendanceDayResponseDTO> days = new ArrayList<>();

        int totalPaidMinutes = 0;

        for (LocalDate date = actualStart; !date.isAfter(endOfMonth); date = date.plusDays(1)) {

            AttendanceRecord r = map.get(date);

            AttendanceDayResponseDTO dto = new AttendanceDayResponseDTO();
            dto.setDate(date);

            if (r != null) {
                dto.setCheckIn(r.getCheckIn());
                dto.setCheckOut(r.getCheckOut());
                dto.setWorkedMinutes(r.getWorkedMinutes());
                dto.setPaidMinutes(r.getPaidMinutes());

                int ot = r.getOtMinutes() != null ? r.getOtMinutes() : 0;
                dto.setOtMinutes(ot);

                dto.setDisplay(
                        AttendanceDisplayUtil.buildDisplay(r)
                );

                if (r.getPaidMinutes() != null) {
                    totalPaidMinutes += r.getPaidMinutes();
                }
            }

            days.add(dto);
        }

        AttendanceMonthlyResponseDTO res =
                new AttendanceMonthlyResponseDTO();

        res.setDays(days);
        res.setTotalPaidMinutes(totalPaidMinutes);
        res.setTotalWorkingDays(totalPaidMinutes / 480.0);

        res.setTotalOTMinutes(totalOTMinutes);
        res.setTotalOTHours(totalOTMinutes / 60.0);

        return res;
    }
}
