package com.tlu.hrm.controller;

import java.time.YearMonth;

import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.dashboard.DashboardOverviewDTO;
import com.tlu.hrm.service.dashboard.DashboardService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Thống kê tổng hợp")
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
