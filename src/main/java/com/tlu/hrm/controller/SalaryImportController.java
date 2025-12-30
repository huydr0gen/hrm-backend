package com.tlu.hrm.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tlu.hrm.dto.SalaryImportResultDTO;
import com.tlu.hrm.service.SalaryImportService;

@RestController
@RequestMapping("/api/salaries")
@PreAuthorize("hasRole('HR')")
public class SalaryImportController {

	private final SalaryImportService importService;

	public SalaryImportController(SalaryImportService importService) {
		super();
		this.importService = importService;
	}
	
	@PostMapping("/import")
    public SalaryImportResultDTO importSalary(
		@RequestParam MultipartFile file,
        @RequestParam int month,
        @RequestParam int year) {

        return importService.importExcel(file, month, year);
    }
}
