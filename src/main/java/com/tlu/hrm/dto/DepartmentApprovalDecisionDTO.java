package com.tlu.hrm.dto;

import com.tlu.hrm.enums.ApprovalStatus;

public class DepartmentApprovalDecisionDTO {

	private ApprovalStatus status; // APPROVED / REJECTED
    private String note;
    
	public DepartmentApprovalDecisionDTO() {
		super();
	}
	
	public ApprovalStatus getStatus() {
		return status;
	}
	public void setStatus(ApprovalStatus status) {
		this.status = status;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
    
}
