package com.tlu.hrm.controller;

import java.time.YearMonth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tlu.hrm.dto.AttendanceImportResultDTO;
import com.tlu.hrm.service.AttendanceImportService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/attendance")
@Tag(name = "Attendance Import")
public class AttendanceImportController {

	private final AttendanceImportService service;

	public AttendanceImportController(AttendanceImportService service) {
		super();
		this.service = service;
	}
	
	// HR IMPORT
    @PostMapping("/import")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<?> importExcel(
            @RequestParam MultipartFile file,
            @RequestParam String month) {

        AttendanceImportResultDTO result =
                service.importExcel(file, YearMonth.parse(month));

        return ResponseEntity.ok(result);
    }

    // HR DOWNLOAD TEMPLATE
    @GetMapping("/template")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<byte[]> downloadTemplate() {

        byte[] file = service.exportTemplate();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=attendance_template.xlsx")
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }
}
