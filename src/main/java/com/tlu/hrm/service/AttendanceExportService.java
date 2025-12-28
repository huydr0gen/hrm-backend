package com.tlu.hrm.service;

import java.time.YearMonth;

public interface AttendanceExportService {

	byte[] exportMonthly(YearMonth month);
}
