package com.tlu.hrm.dto;

import java.util.List;

import com.tlu.hrm.enums.SpecialScheduleStatus;

public class SpecialScheduleBulkApproveDTO {

	private List<Long> ids;
    private SpecialScheduleStatus status;
    
	public SpecialScheduleBulkApproveDTO() {
		super();
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public SpecialScheduleStatus getStatus() {
		return status;
	}

	public void setStatus(SpecialScheduleStatus status) {
		this.status = status;
	}
    
    
}
