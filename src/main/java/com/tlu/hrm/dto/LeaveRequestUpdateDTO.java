package com.tlu.hrm.dto;

import java.time.LocalDate;

import com.tlu.hrm.enums.LeaveStatus;
import com.tlu.hrm.enums.LeaveType;

public class LeaveRequestUpdateDTO {

	private LeaveType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;       // Lý do nhân viên (HR có thể sửa khi cập nhật)
    private String managerNote;
	public LeaveRequestUpdateDTO() {
		super();
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
	public String getManagerNote() {
		return managerNote;
	}
	public void setManagerNote(String managerNote) {
		this.managerNote = managerNote;
	}
    
    
}
