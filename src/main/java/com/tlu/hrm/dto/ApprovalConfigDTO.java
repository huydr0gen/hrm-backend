package com.tlu.hrm.dto;

import com.tlu.hrm.enums.ApprovalTargetType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Thông tin cấu hình người duyệt")
public class ApprovalConfigDTO {

	@Schema(
        description = "ID của cấu hình thiết lập người duyệt",
        example = "10"
    )
    private Long id;

    @Schema(
        description = "Loại đối tượng được áp dụng phê duyệt",
        example = "EMPLOYEE",
        allowableValues = {"EMPLOYEE", "DEPARTMENT"}
    )
    private ApprovalTargetType targetType;

    // ===== LOGIC=====

    @Schema(
        description = "ID đối tượng áp dụng (employeeId hoặc departmentId)",
        example = "5",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long targetId;

    @Schema(
        description = "ID của người duyệt",
        example = "3",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long approverId;

    // =====================================================
    // TARGET DISPLAY (UI / UX)
    // =====================================================

    @Schema(
        description = "Mã đối tượng áp dụng (mã nhân viên hoặc mã phòng ban)",
        example = "EMP005 / DEP003"
    )
    private String targetCode;

    @Schema(
        description = "Tên đối tượng áp dụng (tên nhân viên hoặc tên phòng ban)",
        example = "Nguyễn Văn B / Phòng Công nghệ thông tin"
    )
    private String targetName;

    // =====================================================
    // APPROVER INFO (UI / UX)
    // =====================================================

    @Schema(
        description = "Mã nhân viên của người duyệt",
        example = "EMP003"
    )
    private String approverCode;

    @Schema(
        description = "Tên nhân viên của người duyệt",
        example = "Nguyễn Văn A"
    )
    private String approverName;

    // =====================================================
    // STATUS
    // =====================================================

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

	public String getTargetCode() {
		return targetCode;
	}

	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getApproverCode() {
		return approverCode;
	}

	public void setApproverCode(String approverCode) {
		this.approverCode = approverCode;
	}

	public String getApproverName() {
		return approverName;
	}

	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
    
    

}
