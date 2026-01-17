package com.tlu.hrm.dto;

import java.time.LocalDate;

import com.tlu.hrm.enums.LeaveDuration;
import com.tlu.hrm.enums.LeaveType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu tạo đơn xin nghỉ phép")
public class LeaveRequestCreateDTO {

	@Schema(description = "Loại nghỉ phép", example = "ANNUAL")
    private LeaveType type;
	
	@Schema(description = "Thời lượng nghỉ", example = "FULL_DAY")
	private LeaveDuration duration;

	@Schema(description = "Ngày nghỉ", example = "2025-12-20")
	private LocalDate leaveDate;

    @Schema(description = "Lý do xin nghỉ", example = "Về quê có việc")
    private String reason;

	public LeaveRequestCreateDTO() {
		super();
	}

	public LeaveRequestCreateDTO(LeaveType type, LeaveDuration duration, LocalDate leaveDate, String reason) {
		super();
		this.type = type;
		this.duration = duration;
		this.leaveDate = leaveDate;
		this.reason = reason;
	}

	public LeaveType getType() {
		return type;
	}

	public void setType(LeaveType type) {
		this.type = type;
	}

	public LeaveDuration getDuration() {
		return duration;
	}

	public void setDuration(LeaveDuration duration) {
		this.duration = duration;
	}

	public LocalDate getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(LocalDate leaveDate) {
		this.leaveDate = leaveDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
    
    
    
}
