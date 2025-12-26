package com.tlu.hrm.dto;

import com.tlu.hrm.enums.ApprovalTargetType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "Loại đối tượng được thiết lập người duyệt",
    example = "DEPARTMENT",
    allowableValues = {"EMPLOYEE", "DEPARTMENT"}
)
public class ApprovalConfigCreateDTO {

	@Schema(
        description = "Loại đối tượng được thiết lập người duyệt",
        example = "DEPARTMENT",
        allowableValues = {"EMPLOYEE", "DEPARTMENT"}
    )
	private ApprovalTargetType targetType;
	
	@Schema(
        description = "ID của đối tượng được áp dụng (employeeId hoặc departmentId)",
        example = "1"
    )
    private Long targetId;
	
	@Schema(
        description = "User ID của người duyệt",
        example = "3"
    )
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
