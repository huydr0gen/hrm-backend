package com.tlu.hrm.dto;

import com.tlu.hrm.enums.ApprovalTargetType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu tạo/cập nhật cấu hình người duyệt")
public class ApprovalConfigCreateDTO {

	@Schema(
        description = "ID cấu hình (null = tạo mới, có = cập nhật)",
        example = "10"
    )
    private Long id;

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
