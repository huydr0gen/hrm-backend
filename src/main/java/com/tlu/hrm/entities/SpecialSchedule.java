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

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private LocalDate date;

    private String reason;

    @Enumerated(EnumType.STRING)
    private SpecialScheduleStatus status = SpecialScheduleStatus.PENDING;

    // HR / Manager quyết định
    private Long decidedBy;
    private LocalDateTime decidedAt;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    
	public SpecialSchedule() {
		super();
	}

	public SpecialSchedule(Long id, Employee employee, LocalDate date, String reason, SpecialScheduleStatus status,
			Long decidedBy, LocalDateTime decidedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.employee = employee;
		this.date = date;
		this.reason = reason;
		this.status = status;
		this.decidedBy = decidedBy;
		this.decidedAt = decidedAt;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
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

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	
}
