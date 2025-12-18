package com.tlu.hrm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.tlu.hrm.enums.TimekeepingExplanationStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "timekeeping_explanations")
public class TimekeepingExplanation {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================
    // Employee
    // ========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // ========================
    // Work info
    // ========================
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "original_check_in")
    private LocalTime originalCheckIn;

    @Column(name = "original_check_out")
    private LocalTime originalCheckOut;

    @Column(name = "proposed_check_in")
    private LocalTime proposedCheckIn;

    @Column(name = "proposed_check_out")
    private LocalTime proposedCheckOut;

    // ========================
    // Explanation
    // ========================
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimekeepingExplanationStatus status = TimekeepingExplanationStatus.PENDING;

    // ========================
    // Decision
    // ========================
    @Column(name = "decided_by")
    private Long decidedBy;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "manager_note", columnDefinition = "TEXT")
    private String managerNote;

    // ========================
    // Audit
    // ========================
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

	public TimekeepingExplanation() {
		super();
	}

	public TimekeepingExplanation(Long id, Employee employee, LocalDate workDate, LocalTime originalCheckIn,
			LocalTime originalCheckOut, LocalTime proposedCheckIn, LocalTime proposedCheckOut, String reason,
			TimekeepingExplanationStatus status, Long decidedBy, LocalDateTime decidedAt, String managerNote,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.employee = employee;
		this.workDate = workDate;
		this.originalCheckIn = originalCheckIn;
		this.originalCheckOut = originalCheckOut;
		this.proposedCheckIn = proposedCheckIn;
		this.proposedCheckOut = proposedCheckOut;
		this.reason = reason;
		this.status = status;
		this.decidedBy = decidedBy;
		this.decidedAt = decidedAt;
		this.managerNote = managerNote;
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

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
    
    
}
