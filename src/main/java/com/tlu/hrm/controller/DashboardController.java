package com.tlu.hrm.controller;

import java.time.YearMonth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tlu.hrm.dto.dashboard.DashboardOverviewDTO;
import com.tlu.hrm.service.dashboard.DashboardService;

public class DashboardController {

	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {
		super();
		this.dashboardService = dashboardService;
	}
	
	@GetMapping("/overview")
    public DashboardOverviewDTO overview(@RequestParam String month) {
        return dashboardService.getOverview(YearMonth.parse(month));
    }
}
