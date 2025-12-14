package com.tlu.hrm.dto;

import java.time.LocalDate;

public class SpecialScheduleUpdateDTO {

	private LocalDate date;
    private String reason;
    
	public SpecialScheduleUpdateDTO() {
		super();
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
