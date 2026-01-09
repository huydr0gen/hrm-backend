package com.tlu.hrm.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO đại diện cho một kỳ lương trong danh sách lương của nhân viên")
public class MySalaryListItemDTO {

	@Schema(description = "Tháng của kỳ lương", example = "10")
	private Integer month;
	
	@Schema(description = "Năm của kỳ lương", example = "2025")
    private Integer year;

	@Schema(description = "Tiêu đề hiển thị của kỳ lương", example = "Kỳ lương T10/2025")
    private String title; // "Kỳ lương T10/2025"

	@Schema(description = "Ngày import dữ liệu lương", example = "2025-10-05")
    private LocalDate importedAt;

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDate getImportedAt() {
		return importedAt;
	}

	public void setImportedAt(LocalDate importedAt) {
		this.importedAt = importedAt;
	}
    
    
}
