package com.tlu.hrm.dto;

import com.tlu.hrm.enums.ApprovalTargetType;

public class ApprovalConfigDTO {

	private Long id;
    private ApprovalTargetType targetType;
    private Long targetId;
    private Long approverId;
    private boolean active;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
    
    
}
