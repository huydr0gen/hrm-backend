package com.tlu.hrm.dto;

import java.time.LocalDate;

import com.tlu.hrm.enums.LeaveDuration;
import com.tlu.hrm.enums.LeaveType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu cập nhật đơn nghỉ (nhân viên, chỉ khi PENDING)")
public class LeaveRequestUpdateDTO {

	@Schema(
        description = "Loại nghỉ phép (chỉ cho phép update khi đơn đang PENDING)",
        example = "ANNUAL",
        nullable = true
    )
    private LeaveType type;

    @Schema(
        description = "Thời lượng nghỉ (FULL_DAY, MORNING, AFTERNOON)",
        example = "FULL_DAY",
        nullable = true
    )
    private LeaveDuration duration;
    
    @Schema(
	    description = "Ngày nghỉ (chỉ cho phép update khi đơn đang PENDING)",
	    example = "2025-12-21",
	    nullable = true
	)
	private LocalDate leaveDate;

    @Schema(
        description = "Lý do xin nghỉ (chỉ cho phép update khi đơn đang PENDING)",
        example = "Cập nhật lý do",
        nullable = true
    )
    private String reason;
    
	public LeaveRequestUpdateDTO() {
		super();
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
