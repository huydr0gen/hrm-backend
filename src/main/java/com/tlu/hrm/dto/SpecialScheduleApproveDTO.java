package com.tlu.hrm.dto;

import com.tlu.hrm.enums.SpecialScheduleStatus;

public class SpecialScheduleApproveDTO {

	private SpecialScheduleStatus status;

	public SpecialScheduleApproveDTO() {
		super();
	}

	public SpecialScheduleStatus getStatus() {
		return status;
	}

	public void setStatus(SpecialScheduleStatus status) {
		this.status = status;
	}
	
	
}
