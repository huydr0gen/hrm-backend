package com.tlu.hrm.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.AttendanceDayResponseDTO;
import com.tlu.hrm.dto.AttendanceMonthlyResponseDTO;
import com.tlu.hrm.dto.SpecialScheduleResponseDTO;
import com.tlu.hrm.entities.AttendanceRecord;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.LeaveRequest;
import com.tlu.hrm.entities.SpecialSchedule;
import com.tlu.hrm.enums.LeaveDuration;
import com.tlu.hrm.enums.LeaveStatus;
import com.tlu.hrm.enums.LeaveType;
import com.tlu.hrm.repository.AttendanceRecordRepository;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.LeaveRequestRepository;
import com.tlu.hrm.repository.SpecialScheduleRepository;
import com.tlu.hrm.utils.AttendanceDisplayUtil;

@Service
public class AttendanceQueryServiceImpl implements AttendanceQueryService {

	private final AttendanceRecordRepository attendanceRepo;
	private final EmployeeRepository employeeRepo;
	private final SpecialScheduleRepository specialScheduleRepo;
	private final LeaveRequestRepository leaveRequestRepository;

	public AttendanceQueryServiceImpl(
			AttendanceRecordRepository attendanceRepo, 
			EmployeeRepository employeeRepo,
			SpecialScheduleRepository specialScheduleRepo,
			LeaveRequestRepository leaveRequestRepository) {
		super();
		this.attendanceRepo = attendanceRepo;
		this.employeeRepo = employeeRepo;
		this.specialScheduleRepo = specialScheduleRepo;
		this.leaveRequestRepository = leaveRequestRepository;
	}
	
	@Override
    public AttendanceMonthlyResponseDTO getMonthly(Long employeeId, YearMonth month) {

		Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate onboardDate = employee.getOnboardDate();
        
        if (onboardDate == null) {
            throw new IllegalStateException("Employee onboardDate is null");
        }
        
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
            empty.setAnnualLeaveQuota(0);
            empty.setAnnualLeaveUsed(0);
            empty.setAnnualLeaveRemaining(0);
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
        	            r -> r,
        	            (a, b) -> a 
        	        ));
        
        List<SpecialSchedule> schedules =
        	    specialScheduleRepo.findApprovedByEmployeeAndMonth(
        	        employeeId,
        	        actualStart,
        	        endOfMonth
        	    );
        
        Map<LocalDate, List<SpecialSchedule>> scheduleMap = new HashMap<>();

        for (SpecialSchedule s : schedules) {
            LocalDate start = s.getStartDate().isBefore(actualStart)
                    ? actualStart
                    : s.getStartDate();

            LocalDate end = s.getEndDate().isAfter(endOfMonth)
                    ? endOfMonth
                    : s.getEndDate();

            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                scheduleMap
                        .computeIfAbsent(d, k -> new ArrayList<>())
                        .add(s);
            }
        }

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
            
            List<SpecialSchedule> daySchedules = scheduleMap.get(date);

            if (daySchedules != null) {
                List<SpecialScheduleResponseDTO> dtoList =
                        daySchedules.stream()
                                .map(this::toSpecialScheduleDTO)
                                .collect(Collectors.toList());

                dto.setSpecialSchedules(dtoList);
            } else {
                dto.setSpecialSchedules(Collections.emptyList());
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
        
        int year = month.getYear();
        LocalDate asOfDate = month.atEndOfMonth();

        double quota = getCurrentQuota(employee, asOfDate);
        double used = calculateUsedQuota(employeeId, year);
        double remaining = quota - used;
        
        remaining = Math.round(remaining * 2) / 2.0;

        res.setAnnualLeaveQuota(quota);
        res.setAnnualLeaveUsed(used);
        res.setAnnualLeaveRemaining(Math.max(0, remaining));

        return res;
    }
	
	private double calculateUsedQuota(Long employeeId, int year) {
	    LocalDate startDate = LocalDate.of(year, 1, 1);
	    LocalDate endDate = LocalDate.of(year, 12, 31);

	    List<LeaveRequest> list =
	            leaveRequestRepository.findForQuotaByYear(
	                    employeeId,
	                    LeaveType.ANNUAL,
	                    List.of(LeaveStatus.APPROVED, LeaveStatus.PENDING),
	                    startDate,
	                    endDate
	            );

	    return list.stream()
	            .mapToDouble(lr -> getDurationValue(lr.getDuration()))
	            .sum();
	}
	
	private LocalDate normalizeOnboardStart(LocalDate onboardDate) {
	    if (onboardDate.getDayOfMonth() == 1) {
	        return onboardDate.withDayOfMonth(1);
	    } else {
	        return onboardDate.plusMonths(1).withDayOfMonth(1);
	    }
	}

	private int countMonthsInclusive(LocalDate start, LocalDate end) {
	    LocalDate s = start.withDayOfMonth(1);
	    LocalDate e = end.withDayOfMonth(1);

	    if (s.isAfter(e)) return 0;

	    return (e.getYear() - s.getYear()) * 12
	         + (e.getMonthValue() - s.getMonthValue()) + 1;
	}

	private double getCurrentQuota(Employee emp, LocalDate asOfDate) {
	    int year = asOfDate.getYear();

	    LocalDate onboard = emp.getOnboardDate();
	    LocalDate yearStart = LocalDate.of(year, 1, 1);
	    LocalDate yearEnd = LocalDate.of(year, 12, 31);

	    if (onboard.isAfter(yearEnd)) {
	        return 0;
	    }

	    LocalDate onboardStart = normalizeOnboardStart(onboard);
	    LocalDate effectiveStart = onboardStart.isAfter(yearStart) ? onboardStart : yearStart;

	    return countMonthsInclusive(effectiveStart, asOfDate);
	}
	
	private double getDurationValue(LeaveDuration duration) {
	    if (duration == null) return 1.0;
	    return switch (duration) {
	        case FULL_DAY -> 1.0;
	        case MORNING, AFTERNOON -> 0.5;
	    };
	}
	
	// =========================
    // Mapper
    // =========================
    private SpecialScheduleResponseDTO toSpecialScheduleDTO(SpecialSchedule s) {
        SpecialScheduleResponseDTO dto = new SpecialScheduleResponseDTO();

        dto.setId(s.getId());

        dto.setStartDate(s.getStartDate());
        dto.setEndDate(s.getEndDate());
        dto.setMorningStart(s.getMorningStart());
        dto.setAfternoonEnd(s.getAfternoonEnd());

        dto.setProjectCode(s.getProjectCode());
        dto.setProjectName(s.getProjectName());
        dto.setManagerCode(s.getOnsiteManagerCode());
        dto.setManagerName(s.getOnsiteManagerName());

        dto.setType(s.getType());
        dto.setReason(s.getReason());
        dto.setStatus(s.getStatus());

        dto.setCreatedAt(s.getCreatedAt());

        return dto;
    }
}
