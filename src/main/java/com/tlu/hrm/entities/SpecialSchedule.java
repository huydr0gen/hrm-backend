package com.tlu.hrm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tlu.hrm.enums.SpecialScheduleStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "special_schedules")
public class SpecialSchedule {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private LocalDate date;

    private String shift;

    private String reason;

    @Enumerated(EnumType.STRING)
    private SpecialScheduleStatus status;

    private String createdBy;
    private String approvedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    
	public SpecialSchedule() {
		super();
	}

	public SpecialSchedule(Long id, Long employeeId, LocalDate date, String shift, String reason,
			SpecialScheduleStatus status, String createdBy, String approvedBy, LocalDateTime createdAt,
			LocalDateTime updatedAt, LocalDateTime approvedAt) {
		super();
		this.id = id;
		this.employeeId = employeeId;
		this.date = date;
		this.shift = shift;
		this.reason = reason;
		this.status = status;
		this.createdBy = createdBy;
		this.approvedBy = approvedBy;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.approvedAt = approvedAt;
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getShift() {
		return shift;
	}

	public void setShift(String shift) {
		this.shift = shift;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}

	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}
    
    
}
