package com.tlu.hrm.dto;

import java.time.LocalDate;

import com.tlu.hrm.enums.LeaveType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu cập nhật đơn nghỉ (HR / Admin)")
public class LeaveRequestUpdateDTO {

	@Schema(description = "Loại nghỉ phép", example = "SICK", nullable = true)
	private LeaveType type;
	
	@Schema(description = "Ngày bắt đầu nghỉ", example = "2025-12-21", nullable = true)
    private LocalDate startDate;
	
	@Schema(description = "Ngày kết thúc nghỉ", example = "2025-12-23", nullable = true)
    private LocalDate endDate;
	
	@Schema(
	        description = "Lý do nghỉ (có thể chỉnh sửa)",
	        example = "Cập nhật lý do",
	        nullable = true
	    )
    private String reason;       // Lý do nhân viên (HR có thể sửa khi cập nhật)
    
	@Schema(
	        description = "Ghi chú của HR / Admin",
	        example = "Điều chỉnh theo thực tế",
	        nullable = true
	    )
	private String managerNote;
	public LeaveRequestUpdateDTO() {
		super();
	}
	public LeaveType getType() {
		return type;
	}
	public void setType(LeaveType type) {
		this.type = type;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getManagerNote() {
		return managerNote;
	}
	public void setManagerNote(String managerNote) {
		this.managerNote = managerNote;
	}
    
    
}
