package com.tlu.hrm.service;

import java.time.YearMonth;

import org.springframework.web.multipart.MultipartFile;

import com.tlu.hrm.dto.AttendanceImportResultDTO;

public interface AttendanceImportService {

	AttendanceImportResultDTO importExcel(MultipartFile file, YearMonth month);
}
