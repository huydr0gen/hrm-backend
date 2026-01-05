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
        description = "Mã đối tượng được áp dụng (employeeCode hoặc departmentCode)",
        example = "EMP005 / IT"
    )
    private String targetCode;

    @Schema(
        description = "Mã nhân viên của người duyệt",
        example = "EMP003"
    )
    private String approverCode;

	public ApprovalTargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(ApprovalTargetType targetType) {
		this.targetType = targetType;
	}

	public String getTargetCode() {
		return targetCode;
	}

	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}

	public String getApproverCode() {
		return approverCode;
	}

	public void setApproverCode(String approverCode) {
		this.approverCode = approverCode;
	}
	
    
    
}
