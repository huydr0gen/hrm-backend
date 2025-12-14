package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tlu.hrm.enums.SpecialScheduleStatus;

public class SpecialScheduleResponseDTO {

	private Long id;
    private Long employeeId;
    private String employeeName;
    private String department;

    private LocalDate date;
    private String reason;

    private SpecialScheduleStatus status;
    private Long decidedBy;
    private LocalDateTime decidedAt;

    private LocalDateTime createdAt;
    
	public SpecialScheduleResponseDTO() {
		super();
	}

	public SpecialScheduleResponseDTO(Long id, Long employeeId, String employeeName, String department, LocalDate date,
			String reason, SpecialScheduleStatus status, Long decidedBy, LocalDateTime decidedAt,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.department = department;
		this.date = date;
		this.reason = reason;
		this.status = status;
		this.decidedBy = decidedBy;
		this.decidedAt = decidedAt;
		this.createdAt = createdAt;
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public SpecialScheduleStatus getStatus() {
		return status;
	}

	public void setStatus(SpecialScheduleStatus status) {
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
}
