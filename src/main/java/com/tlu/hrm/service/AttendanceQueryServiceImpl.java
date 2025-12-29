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
import com.tlu.hrm.repository.AttendanceRecordRepository;
import com.tlu.hrm.utils.AttendanceDisplayUtil;

@Service
public class AttendanceQueryServiceImpl implements AttendanceQueryService {

	private final AttendanceRecordRepository attendanceRepo;

	public AttendanceQueryServiceImpl(AttendanceRecordRepository attendanceRepo) {
		super();
		this.attendanceRepo = attendanceRepo;
	}
	
	@Override
    public AttendanceMonthlyResponseDTO getMonthly(
            Long employeeId,
            YearMonth month) {

        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        List<AttendanceRecord> records =
                attendanceRepo.findMonthly(employeeId, start, end);
        
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

        for (int d = 1; d <= month.lengthOfMonth(); d++) {

            LocalDate date = month.atDay(d);
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

                // display (giữ logic cũ + thêm OT)
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

        // ===============================
        // OT SUMMARY
        // ===============================
        res.setTotalOTMinutes(totalOTMinutes);
        res.setTotalOTHours(totalOTMinutes / 60.0);

        return res;
    }
}
