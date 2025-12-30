package com.tlu.hrm.service.dashboard;

import java.time.YearMonth;

import com.tlu.hrm.dto.dashboard.DashboardOverviewDTO;

public interface DashboardService {

	DashboardOverviewDTO getOverview(YearMonth month);
}
