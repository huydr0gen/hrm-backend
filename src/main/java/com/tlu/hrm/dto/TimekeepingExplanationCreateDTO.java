package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TimekeepingExplanationCreateDTO {

	@NotNull
    private LocalDate workDate;

    private LocalTime proposedCheckIn;
    private LocalTime proposedCheckOut;

    @NotBlank
    private String reason;

	public LocalDate getWorkDate() {
		return workDate;
	}

	public void setWorkDate(LocalDate workDate) {
		this.workDate = workDate;
	}

	public LocalTime getProposedCheckIn() {
		return proposedCheckIn;
	}

	public void setProposedCheckIn(LocalTime proposedCheckIn) {
		this.proposedCheckIn = proposedCheckIn;
	}

	public LocalTime getProposedCheckOut() {
		return proposedCheckOut;
	}

	public void setProposedCheckOut(LocalTime proposedCheckOut) {
		this.proposedCheckOut = proposedCheckOut;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
    
    
}
