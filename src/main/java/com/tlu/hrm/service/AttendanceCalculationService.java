package com.tlu.hrm.service;

import java.time.LocalDate;
import java.time.YearMonth;

public interface AttendanceCalculationService {

	// Tính lại công cho 1 ngày của 1 nhân viên
    void recalculateDaily(Long employeeId, LocalDate date);

    // Tính lại công cho cả tháng
    void recalculateMonthly(Long employeeId, YearMonth month);
}
