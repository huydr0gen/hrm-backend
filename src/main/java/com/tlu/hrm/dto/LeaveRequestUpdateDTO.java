package com.tlu.hrm.dto;

import java.time.LocalDate;

import com.tlu.hrm.enums.LeaveType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu cập nhật đơn nghỉ (nhân viên, chỉ khi PENDING)")
public class LeaveRequestUpdateDTO {

	@Schema(
        description = "Ngày bắt đầu nghỉ (chỉ cho phép update khi đơn đang PENDING)",
        example = "2025-12-21",
        nullable = true
    )
    private LocalDate startDate;

    @Schema(
        description = "Ngày kết thúc nghỉ (chỉ cho phép update khi đơn đang PENDING)",
        example = "2025-12-23",
        nullable = true
    )
    private LocalDate endDate;

    @Schema(
        description = "Lý do xin nghỉ (chỉ cho phép update khi đơn đang PENDING)",
        example = "Cập nhật lý do",
        nullable = true
    )
    private String reason;
    
	public LeaveRequestUpdateDTO() {
		super();
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
	
    
}
