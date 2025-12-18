package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tlu.hrm.enums.SpecialScheduleType;

public class SpecialScheduleCreateDTO {

	private Long employeeId;

    // ===== Date range =====
    private LocalDate startDate;
    private LocalDate endDate;

    // ===== On-site time (optional) =====
    private LocalTime morningStart;
    private LocalTime morningEnd;

    private LocalTime afternoonStart;
    private LocalTime afternoonEnd;

    // ===== Type =====
    private SpecialScheduleType type;

    private String reason;
    
	public SpecialScheduleCreateDTO() {
		super();
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
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

	public LocalTime getMorningStart() {
		return morningStart;
	}

	public void setMorningStart(LocalTime morningStart) {
		this.morningStart = morningStart;
	}

	public LocalTime getMorningEnd() {
		return morningEnd;
	}

	public void setMorningEnd(LocalTime morningEnd) {
		this.morningEnd = morningEnd;
	}

	public LocalTime getAfternoonStart() {
		return afternoonStart;
	}

	public void setAfternoonStart(LocalTime afternoonStart) {
		this.afternoonStart = afternoonStart;
	}

	public LocalTime getAfternoonEnd() {
		return afternoonEnd;
	}

	public void setAfternoonEnd(LocalTime afternoonEnd) {
		this.afternoonEnd = afternoonEnd;
	}

	public SpecialScheduleType getType() {
		return type;
	}

	public void setType(SpecialScheduleType type) {
		this.type = type;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	
	
}
