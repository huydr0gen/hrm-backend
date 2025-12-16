package com.tlu.hrm.dto;

import com.tlu.hrm.enums.PersonalApprovalType;

public class PersonalApprovalCreateDTO {

	private PersonalApprovalType type;
    private String reason;
    
	public PersonalApprovalCreateDTO() {
		super();
	}
	
	public PersonalApprovalType getType() {
		return type;
	}
	public void setType(PersonalApprovalType type) {
		this.type = type;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}

    
}
