package com.tlu.hrm.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlu.hrm.entities.AttendanceRecord;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.SpecialSchedule;
import com.tlu.hrm.enums.AttendanceWorkType;
import com.tlu.hrm.enums.SpecialScheduleType;
import com.tlu.hrm.repository.AttendanceRecordRepository;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.LeaveRequestRepository;
import com.tlu.hrm.repository.SpecialScheduleRepository;
import com.tlu.hrm.repository.TimekeepingExplanationRepository;

@Service
@Transactional
public class AttendanceCalculationServiceImpl implements AttendanceCalculationService {

	private static final int FULL_DAY_MINUTES = 480;
    private static final int HALF_DAY_MINUTES = 240;

    private final AttendanceRecordRepository attendanceRepo;
    private final LeaveRequestRepository leaveRepo;
    private final SpecialScheduleRepository specialScheduleRepo;
    private final TimekeepingExplanationRepository explanationRepo;
    private final EmployeeRepository employeeRepo;
    
	public AttendanceCalculationServiceImpl(AttendanceRecordRepository attendanceRepo, LeaveRequestRepository leaveRepo,
			SpecialScheduleRepository specialScheduleRepo, TimekeepingExplanationRepository explanationRepo, EmployeeRepository employeeRepo) {
		super();
		this.attendanceRepo = attendanceRepo;
		this.leaveRepo = leaveRepo;
		this.specialScheduleRepo = specialScheduleRepo;
		this.explanationRepo = explanationRepo;
		this.employeeRepo = employeeRepo;
	}
    
	// =====================================================
    // TÍNH CÔNG CHO 1 NGÀY
    // =====================================================
    @Override
    public void recalculateDaily(Long employeeId, LocalDate date) {

        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        if (date.isBefore(employee.getOnboardDate())) {
        	throw new RuntimeException("Không thể tính công cho ngày trước khi nhân viên onboard");
        }
        
        AttendanceRecord record = attendanceRepo
                .findByEmployeeIdAndWorkDate(employeeId, date)
                .orElse(null);

        // =================================================
        // 1️⃣ LEAVE APPROVED → override toàn bộ công
        // =================================================
        boolean hasApprovedLeave = leaveRepo.existsApprovedOverlap(employeeId, date, date);

        if (hasApprovedLeave) {

            if (record == null) {
                record = new AttendanceRecord();
                record.setEmployee(employee);
                record.setWorkDate(date);
                // employee PHẢI được set từ nơi tạo record ban đầu
            }

            record.setPaidMinutes(FULL_DAY_MINUTES);
            record.setWorkType(AttendanceWorkType.FULL_DAY);
            attendanceRepo.save(record);
            return;
        }

        // =================================================
        // 2️⃣ SPECIAL SCHEDULE APPROVED
        // =================================================
        List<SpecialSchedule> schedules = specialScheduleRepo.findApprovedSchedulesByEmployeeAndDate(employee, date);

        if (!schedules.isEmpty()) {

            if (record == null) {
                record = new AttendanceRecord();
                record.setEmployee(employee);
                record.setWorkDate(date);
                record.setWorkedMinutes(0);
            }

            SpecialSchedule ss = schedules.get(0);

            // Riêng cho lịch con nhỏ
            if (ss.getType() == SpecialScheduleType.CHILD_CARE) {

                int requiredMinutes = ss.getWorkingHours() * 60; // 7h = 420
                int workedMinutes = record.getWorkedMinutes() != null
                        ? record.getWorkedMinutes()
                        : 0;

                if (workedMinutes >= requiredMinutes) {
                    record.setPaidMinutes(FULL_DAY_MINUTES); // vẫn tính 8h
                    record.setWorkType(AttendanceWorkType.FULL_DAY);
                } else {
                    record.setPaidMinutes(0);
                    record.setWorkType(AttendanceWorkType.ABSENT);
                }

                attendanceRepo.save(record);
                return;
            }

            // Các loại lịch đặc thù khác → giữ logic cũ
            record.setPaidMinutes(FULL_DAY_MINUTES);
            record.setWorkType(AttendanceWorkType.FULL_DAY);
            attendanceRepo.save(record);
            return;
        }

        // =================================================
        // 3️⃣ TIMEKEEPING EXPLANATION APPROVED
        // =================================================
        var explanationOpt = explanationRepo.findApprovedByEmployeeAndDate(employeeId, date);

        if (explanationOpt.isPresent()) {

            if (record == null) {
                record = new AttendanceRecord();
                record.setEmployee(employee);
                record.setWorkDate(date);
            }

            // Với đồ án: giải trình hợp lệ → tính đủ công
            record.setPaidMinutes(FULL_DAY_MINUTES);
            record.setWorkType(AttendanceWorkType.FULL_DAY);
            attendanceRepo.save(record);
            return;
        }

        // =================================================
        // 4️⃣ DỮ LIỆU THÔ (CHECK IN / CHECK OUT)
        // =================================================
        if (record == null) {
            record = new AttendanceRecord();
            record.setEmployee(employee);
            record.setWorkDate(date);
            record.setWorkedMinutes(0);
        }
        
        int workedMinutes = record.getWorkedMinutes() != null
                ? record.getWorkedMinutes()
                : 0;

        if (workedMinutes >= FULL_DAY_MINUTES) {
            record.setPaidMinutes(FULL_DAY_MINUTES);
            record.setWorkType(AttendanceWorkType.FULL_DAY);
        } else if (workedMinutes >= HALF_DAY_MINUTES) {
            record.setPaidMinutes(HALF_DAY_MINUTES);
            record.setWorkType(AttendanceWorkType.HALF_DAY);
        } else {
            record.setPaidMinutes(0);
            record.setWorkType(AttendanceWorkType.ABSENT);
        }

        attendanceRepo.save(record);
    }

    // =====================================================
    // TÍNH CÔNG THEO THÁNG
    // =====================================================
    @Override
    public void recalculateMonthly(Long employeeId, YearMonth month) {

        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate onboardDate = employee.getOnboardDate();
        LocalDate startOfMonth = month.atDay(1);
        LocalDate endOfMonth = month.atEndOfMonth();

        // Nếu nhân viên onboard sau tháng này → không tính công
        if (onboardDate.isAfter(endOfMonth)) {
            return;
        }

        // Ngày bắt đầu thực tế để tính công
        LocalDate actualStart = onboardDate.isAfter(startOfMonth)
                ? onboardDate
                : startOfMonth;

        for (LocalDate date = actualStart; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
            recalculateDaily(employeeId, date);
        }
    }
    
    @Override
    public void addOTMinutes(Long employeeId, LocalDate date, int otMinutes) {

        if (otMinutes <= 0) return;

        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 🚫 CHẶN OT TRƯỚC ONBOARD
        if (date.isBefore(employee.getOnboardDate())) {
        	throw new RuntimeException("Không thể thêm OT cho ngày trước khi nhân viên onboard");
        }

        AttendanceRecord record = attendanceRepo
                .findByEmployeeIdAndWorkDate(employeeId, date)
                .orElse(null);

        if (record == null) {
            record = new AttendanceRecord();
            record.setEmployee(employee);
            record.setWorkDate(date);
            record.setWorkedMinutes(0);
            record.setPaidMinutes(0);
            record.setWorkType(AttendanceWorkType.ABSENT);
        }

        int currentOT = record.getOtMinutes() != null
                ? record.getOtMinutes()
                : 0;

        record.setOtMinutes(currentOT + otMinutes);

        attendanceRepo.save(record);
    }
}
