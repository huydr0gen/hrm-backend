package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.tlu.hrm.enums.TimekeepingExplanationStatus;

public class TimekeepingExplanationResponseDTO {

	private Long id;

    // =====================
    // Employee info
    // =====================
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;

    // =====================
    // Work info
    // =====================
    private LocalDate workDate;
    private LocalTime originalCheckIn;
    private LocalTime originalCheckOut;
    private LocalTime proposedCheckIn;
    private LocalTime proposedCheckOut;

    // =====================
    // Explanation
    // =====================
    private String reason;
    private TimekeepingExplanationStatus status;

    // =====================
    // Decision info
    // =====================
    private Long decidedBy;
    private LocalDateTime decidedAt;
    private String managerNote;

    private LocalDateTime createdAt;

	public TimekeepingExplanationResponseDTO() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public LocalDate getWorkDate() {
		return workDate;
	}

	public void setWorkDate(LocalDate workDate) {
		this.workDate = workDate;
	}

	public LocalTime getOriginalCheckIn() {
		return originalCheckIn;
	}

	public void setOriginalCheckIn(LocalTime originalCheckIn) {
		this.originalCheckIn = originalCheckIn;
	}

	public LocalTime getOriginalCheckOut() {
		return originalCheckOut;
	}

	public void setOriginalCheckOut(LocalTime originalCheckOut) {
		this.originalCheckOut = originalCheckOut;
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

	public TimekeepingExplanationStatus getStatus() {
		return status;
	}

	public void setStatus(TimekeepingExplanationStatus status) {
		this.status = status;
	}

	public Long getDecidedBy() {
		return decidedBy;
	}

	public void setDecidedBy(Long decidedBy) {
		this.decidedBy = decidedBy;
	}

	public LocalDateTime getDecidedAt() {
		return decidedAt;
	}

	public void setDecidedAt(LocalDateTime decidedAt) {
		this.decidedAt = decidedAt;
	}

	public String getManagerNote() {
		return managerNote;
	}

	public void setManagerNote(String managerNote) {
		this.managerNote = managerNote;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}
