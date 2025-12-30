package com.tlu.hrm.service;

import org.springframework.web.multipart.MultipartFile;

import com.tlu.hrm.dto.SalaryImportResultDTO;

public interface SalaryImportService {

	SalaryImportResultDTO importExcel(MultipartFile file, int month, int year);
}
