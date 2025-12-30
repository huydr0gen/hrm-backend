package com.tlu.hrm.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.MySalaryResponseDTO;
import com.tlu.hrm.service.SalaryService;

@RestController
@RequestMapping("/api/salaries")
@PreAuthorize("hasRole('EMPLOYEE')")
public class SalaryController {

	private final SalaryService salaryService;

	public SalaryController(SalaryService salaryService) {
		super();
		this.salaryService = salaryService;
	}
	
	@GetMapping("/my")
    public MySalaryResponseDTO getMySalary(
        @RequestParam int month,
        @RequestParam int year) {

        return salaryService.getMySalary(month, year);
    }
}
