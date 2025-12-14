package com.tlu.hrm.dto;

import java.time.LocalDate;

import com.tlu.hrm.enums.LeaveType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu tạo đơn xin nghỉ phép")
public class LeaveRequestCreateDTO {

	@Schema(
	        description = "ID nhân viên",
	        example = "5",
	        nullable = true
	    )
	private Long employeeId;
	
	@Schema(description = "Loại nghỉ phép", example = "ANNUAL")
    private LeaveType type;
	
	@Schema(description = "Ngày bắt đầu nghỉ", example = "2025-12-20")
    private LocalDate startDate;
	
	@Schema(description = "Ngày kết thúc nghỉ", example = "2025-12-22")
    private LocalDate endDate;
	
	@Schema(description = "Lý do xin nghỉ", example = "Về quê có việc")
    private String reason;
    
	public LeaveRequestCreateDTO() {
		super();
	}
	
	public LeaveRequestCreateDTO(Long employeeId, LeaveType type, LocalDate startDate, LocalDate endDate,
			String reason) {
		super();
		this.employeeId = employeeId;
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
		this.reason = reason;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
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
    
    
}
