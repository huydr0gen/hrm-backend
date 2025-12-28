package com.tlu.hrm.service;

import java.time.YearMonth;
import com.tlu.hrm.dto.AttendanceMonthlyResponseDTO;

public interface AttendanceQueryService {

	AttendanceMonthlyResponseDTO getMonthly(
            Long employeeId,
            YearMonth month
    );
}
