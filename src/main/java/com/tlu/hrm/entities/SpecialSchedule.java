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

    // =========================
    // Employee
    // =========================
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
    // Working time (ON_SITE / CHILD_CARE)
    // =========================
    @Column(name = "morning_start")
    private LocalTime morningStart;

    @Column(name = "morning_end")
    private LocalTime morningEnd;

    @Column(name = "afternoon_start")
    private LocalTime afternoonStart;

    @Column(name = "afternoon_end")
    private LocalTime afternoonEnd;

    /**
     * Số giờ làm việc/ngày.
     * - CHILD_CARE: = 7
     * - Các loại khác: null
     * Dùng cho chức năng import & tính công sau này.
     */
    @Column(name = "working_hours")
    private Integer workingHours;

    // =========================
    // ON_SITE project info
    // =========================

    /**
     * Mã dự án (chỉ áp dụng cho ON_SITE)
     */
    @Column(name = "project_code")
    private String projectCode;

    /**
     * Tên dự án (chỉ áp dụng cho ON_SITE)
     */
    @Column(name = "project_name")
    private String projectName;

    /**
     * Mã nhân viên của quản lý dự án (ON_SITE)
     */
    @Column(name = "onsite_manager_code")
    private String onsiteManagerCode;

    /**
     * Tên quản lý dự án (ON_SITE)
     */
    @Column(name = "onsite_manager_name")
    private String onsiteManagerName;

    // =========================
    // Type & reason
    // =========================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpecialScheduleType type;

    private String reason;

    // =========================
    // Approval
    // =========================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpecialScheduleStatus status = SpecialScheduleStatus.PENDING;

    /**
     * User ID của người duyệt (được resolve theo cấu hình duyệt)
     */
    @Column(name = "approver_id")
    private Long approverId;

    /**
     * User ID của người đã duyệt / từ chối
     */
    @Column(name = "decided_by")
    private Long decidedBy;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    // =========================
    // Audit
    // =========================
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getOnsiteManagerCode() {
		return onsiteManagerCode;
	}

	public void setOnsiteManagerCode(String onsiteManagerCode) {
		this.onsiteManagerCode = onsiteManagerCode;
	}

	public String getOnsiteManagerName() {
		return onsiteManagerName;
	}

	public void setOnsiteManagerName(String onsiteManagerName) {
		this.onsiteManagerName = onsiteManagerName;
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
