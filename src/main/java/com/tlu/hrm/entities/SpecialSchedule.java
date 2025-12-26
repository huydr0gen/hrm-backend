package com.tlu.hrm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.tlu.hrm.enums.SpecialScheduleStatus;
import com.tlu.hrm.enums.SpecialScheduleType;

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

    // =========================
    // Date range
    // =========================
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // =========================
    // On-site time range
    // =========================
    private LocalTime morningStart;
    private LocalTime morningEnd;

    private LocalTime afternoonStart;
    private LocalTime afternoonEnd;
    
    // Số giờ làm việc/ngày (dùng cho import công)
    @Column(name = "working_hours")
    private Integer workingHours;

    // =========================
    // Type & Reason
    // =========================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpecialScheduleType type;

    private String reason;

    // =========================
    // Approval
    // =========================
    @Enumerated(EnumType.STRING)
    private SpecialScheduleStatus status = SpecialScheduleStatus.PENDING;

    @Column(name = "approver_id")
    private Long approverId;
    
    private Long decidedBy;
    private LocalDateTime decidedAt;

    // =========================
    // Audit
    // =========================
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
	public SpecialSchedule() {
		super();
	}

	public SpecialSchedule(Long id, Employee employee, LocalDate startDate, LocalDate endDate, LocalTime morningStart,
			LocalTime morningEnd, LocalTime afternoonStart, LocalTime afternoonEnd, Integer workingHours,
			SpecialScheduleType type, String reason, SpecialScheduleStatus status, Long approverId, Long decidedBy,
			LocalDateTime decidedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.employee = employee;
		this.startDate = startDate;
		this.endDate = endDate;
		this.morningStart = morningStart;
		this.morningEnd = morningEnd;
		this.afternoonStart = afternoonStart;
		this.afternoonEnd = afternoonEnd;
		this.workingHours = workingHours;
		this.type = type;
		this.reason = reason;
		this.status = status;
		this.approverId = approverId;
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

	public Integer getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(Integer workingHours) {
		this.workingHours = workingHours;
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

	public SpecialScheduleStatus getStatus() {
		return status;
	}

	public void setStatus(SpecialScheduleStatus status) {
		this.status = status;
	}

	public Long getApproverId() {
		return approverId;
	}

	public void setApproverId(Long approverId) {
		this.approverId = approverId;
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
