package com.tlu.hrm.dto;

import java.time.LocalDate;

public class SpecialScheduleCreateDTO {

	private Long employeeId;
    private LocalDate date;
    private String reason;
    
	public SpecialScheduleCreateDTO() {
		super();
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
    
    
}
