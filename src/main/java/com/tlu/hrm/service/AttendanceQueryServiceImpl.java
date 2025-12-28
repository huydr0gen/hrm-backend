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

	    Map<LocalDate, AttendanceRecord> map =
	            records.stream()
	                   .collect(Collectors.toMap(
	                       AttendanceRecord::getWorkDate,
	                       r -> r
	                   ));

	    List<AttendanceDayResponseDTO> days = new ArrayList<>();

	    for (int d = 1; d <= month.lengthOfMonth(); d++) {

	        LocalDate date = month.atDay(d);
	        AttendanceRecord r = map.get(date);

	        AttendanceDayResponseDTO dto =
	                new AttendanceDayResponseDTO();

	        dto.setDate(date);

	        if (r != null) {
	            dto.setCheckIn(r.getCheckIn());
	            dto.setCheckOut(r.getCheckOut());
	            dto.setWorkedMinutes(r.getWorkedMinutes());
	            dto.setPaidMinutes(r.getPaidMinutes());
	            dto.setDisplay(
	                    AttendanceDisplayUtil.buildDisplay(r)
	            );
	        }

	        days.add(dto);
	    }

	    // ===============================
	    // TÍNH TỔNG NGÀY CÔNG THÁNG
	    // ===============================
	    int totalPaidMinutes = days.stream()
	            .map(AttendanceDayResponseDTO::getPaidMinutes)
	            .filter(m -> m != null)
	            .mapToInt(Integer::intValue)
	            .sum();

	    AttendanceMonthlyResponseDTO res =
	            new AttendanceMonthlyResponseDTO();

	    res.setDays(days);
	    res.setTotalPaidMinutes(totalPaidMinutes);
	    res.setTotalWorkingDays(totalPaidMinutes / 480.0);

	    return res;
	}
}
