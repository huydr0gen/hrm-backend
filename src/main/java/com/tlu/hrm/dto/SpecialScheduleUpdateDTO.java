package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class SpecialScheduleUpdateDTO {

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalTime morningStart;
    private LocalTime morningEnd;

    private LocalTime afternoonStart;
    private LocalTime afternoonEnd;

    private String reason;

    
	public SpecialScheduleUpdateDTO() {
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


	public String getReason() {
		return reason;
	}


	public void setReason(String reason) {
		this.reason = reason;
	}

	
    
}
