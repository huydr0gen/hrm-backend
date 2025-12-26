package com.tlu.hrm.dto;

import com.tlu.hrm.enums.ApprovalTargetType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Thông tin cấu hình người duyệt")
public class ApprovalConfigDTO {

	@Schema(description = "ID của cấu hình", example = "10")
	private Long id;
	
	@Schema(
        description = "Loại đối tượng được áp dụng",
        example = "EMPLOYEE"
    )
    private ApprovalTargetType targetType;
	
	@Schema(
        description = "ID của nhân viên hoặc phòng ban",
        example = "5"
    )
    private Long targetId;
	
	@Schema(
        description = "User ID của người duyệt",
        example = "3"
    )
    private Long approverId;
	
	@Schema(
        description = "Trạng thái kích hoạt của cấu hình",
        example = "true"
    )
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
