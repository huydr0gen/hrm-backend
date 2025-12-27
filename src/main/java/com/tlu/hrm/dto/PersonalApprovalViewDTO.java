package com.tlu.hrm.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "PersonalApprovalViewDTO",
    description = "Thông tin cấu hình người duyệt cho cá nhân nhân viên"
)
public class PersonalApprovalViewDTO {

	@Schema(
        description = "ID nhân viên được thiết lập người duyệt",
        example = "10"
    )
    private Long employeeId;

    @Schema(
        description = "User ID của người duyệt",
        example = "3"
    )
    private Long approverId;

    @Schema(
        description = "Thời điểm thiết lập người duyệt",
        example = "2025-12-21T10:15:00"
    )
    private LocalDateTime createdAt;

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Long getApproverId() {
		return approverId;
	}

	public void setApproverId(Long approverId) {
		this.approverId = approverId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}
