package com.tlu.hrm.dto;

import com.tlu.hrm.enums.ApprovalTargetType;

public class ApprovalConfigCreateDTO {

	private ApprovalTargetType targetType;
    private Long targetId;
    private Long approverId;
	public ApprovalTargetType getTargetType() {
		return targetType;
	}
	public void setTargetType(ApprovalTargetType targetType) {
		this.targetType = targetType;
	}
	public Long getTargetId() {
		return targetId;
	}
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
	public Long getApproverId() {
		return approverId;
	}
	public void setApproverId(Long approverId) {
		this.approverId = approverId;
	}
    
    
}
