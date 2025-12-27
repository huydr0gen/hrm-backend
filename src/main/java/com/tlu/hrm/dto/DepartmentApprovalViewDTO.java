package com.tlu.hrm.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "DepartmentApprovalViewDTO",
    description = "Thông tin cấu hình người duyệt theo phòng ban"
)
public class DepartmentApprovalViewDTO {
	@Schema(
        description = "ID phòng ban",
        example = "1"
    )
    private Long departmentId;

    @Schema(
        description = "User ID của người duyệt",
        example = "5"
    )
    private Long approverId;

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
