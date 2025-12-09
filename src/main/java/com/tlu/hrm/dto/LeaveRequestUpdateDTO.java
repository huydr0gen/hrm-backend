package com.tlu.hrm.dto;

import java.time.LocalDate;

import com.tlu.hrm.enums.LeaveStatus;

public class LeaveRequestUpdateDTO {

	private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public LeaveStatus getStatus() {
		return status;
	}
	public void setStatus(LeaveStatus status) {
		this.status = status;
	}
    
    
	
}
