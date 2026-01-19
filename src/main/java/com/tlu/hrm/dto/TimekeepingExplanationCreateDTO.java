package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TimekeepingExplanationCreateDTO {

	@NotNull
    private LocalDate workDate;

	@JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", example = "08:00:00")
    private LocalTime proposedCheckIn;
	
	@JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", example = "17:00:00")
    private LocalTime proposedCheckOut;

    @NotBlank
    @Schema(example = "Đi trễ do tắc đường")
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
