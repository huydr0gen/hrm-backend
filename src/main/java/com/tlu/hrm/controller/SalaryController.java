package com.tlu.hrm.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tlu.hrm.dto.MySalaryResponseDTO;
import com.tlu.hrm.repository.SalaryRecordRepository;
import com.tlu.hrm.service.SalaryService;

@RestController
@RequestMapping("/api/salaries")
@PreAuthorize("hasRole('EMPLOYEE')")
public class SalaryController {

	private final SalaryService salaryService;
	private final SalaryRecordRepository salaryRecordRepository;

	public SalaryController(SalaryService salaryService, SalaryRecordRepository salaryRecordRepository) {
		super();
		this.salaryService = salaryService;
		this.salaryRecordRepository = salaryRecordRepository;
	}
	
	@GetMapping("/my")
    public MySalaryResponseDTO getMySalary(
        @RequestParam int month,
        @RequestParam int year) {

        return salaryService.getMySalary(month, year);
    }
	
	@DeleteMapping
	@PreAuthorize("hasRole('HR')")
	public void deleteSalaryByMonth(
	    @RequestParam int month,
	    @RequestParam int year
	) {
		salaryRecordRepository.deleteByMonthAndYear(month, year);
	}
}
