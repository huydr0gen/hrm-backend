package com.tlu.hrm.dto;

import com.tlu.hrm.enums.ApprovalStatus;

public class PersonalApprovalDecisionDTO {

	private ApprovalStatus status; // APPROVED hoặc REJECTED
    private String note;
    
	public PersonalApprovalDecisionDTO() {
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
