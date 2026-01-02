package com.tlu.hrm.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "DepartmentApprovalViewDTO",
    description = "Thông tin cấu hình người duyệt theo phòng ban"
)
public class DepartmentApprovalViewDTO {
	@Schema(
        description = "ID phòng ban được áp dụng cấu hình duyệt",
        example = "1"
    )
    private Long departmentId;

    @Schema(
        description = "Mã phòng ban",
        example = "IT"
    )
    private String departmentCode;

    @Schema(
        description = "Tên phòng ban",
        example = "Phòng Công nghệ thông tin"
    )
    private String departmentName;
    
    @Schema(
        description = "ID của người duyệt",
        example = "5"
    )
    private Long approverId;

    @Schema(
        description = "Mã nhân viên của người duyệt",
        example = "EMP005"
    )
    private String approverCode;

    @Schema(
        description = "Tên nhân viên của người duyệt",
        example = "Nguyễn Văn A"
    )
    private String approverName;

    @Schema(
        description = "Thời điểm thiết lập hoặc cập nhật người duyệt",
        example = "2025-12-20T09:30:00"
    )
    private LocalDateTime createdAt;

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Long getApproverId() {
		return approverId;
	}

	public void setApproverId(Long approverId) {
		this.approverId = approverId;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	
    
}
