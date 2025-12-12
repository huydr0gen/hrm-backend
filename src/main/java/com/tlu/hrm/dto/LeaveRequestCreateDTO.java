package com.tlu.hrm.dto;

import java.time.LocalDate;

import com.tlu.hrm.enums.LeaveType;

public class LeaveRequestCreateDTO {

	private Long employeeId;
    private LeaveType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    
	public LeaveRequestCreateDTO() {
		super();
	}
	
	public LeaveRequestCreateDTO(Long employeeId, LeaveType type, LocalDate startDate, LocalDate endDate,
			String reason) {
		super();
		this.employeeId = employeeId;
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
		this.reason = reason;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public LeaveType getType() {
		return type;
	}

	public void setType(LeaveType type) {
		this.type = type;
	}

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
    
    
}
