package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class OTRequestCreateDTO {

	private LocalDate otDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
    private List<String> employeeCodes;
    
	public LocalDate getOtDate() {
		return otDate;
	}
	public void setOtDate(LocalDate otDate) {
		this.otDate = otDate;
	}
	public LocalTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}
	public LocalTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public List<String> getEmployeeCodes() {
		return employeeCodes;
	}
	public void setEmployeeCodes(List<String> employeeIds) {
		this.employeeCodes = employeeIds;
	}
    
    
}
