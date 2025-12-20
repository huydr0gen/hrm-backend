package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.tlu.hrm.enums.SpecialScheduleStatus;
import com.tlu.hrm.enums.SpecialScheduleType;

public class SpecialScheduleResponseDTO {

	private Long id;

    // ===== Employee info =====
    private Long employeeId;
    private String employeeCode;
    private String employeeName;

    private Long departmentId;
    private String departmentName;

    // ===== Date range =====
    private LocalDate startDate;
    private LocalDate endDate;

    // ===== On-site time =====
    private LocalTime morningStart;
    private LocalTime morningEnd;

    private LocalTime afternoonStart;
    private LocalTime afternoonEnd;

    // ===== Type & Reason =====
    private SpecialScheduleType type;
    private String reason;

    // ===== Approval =====
    private SpecialScheduleStatus status;
    private Long approverId;
    private Long decidedBy;
    private LocalDateTime decidedAt;

    // ===== Audit =====
    private LocalDateTime createdAt;

	public SpecialScheduleResponseDTO() {
		super();
	}

	public SpecialScheduleResponseDTO(Long id, Long employeeId, String employeeCode, String employeeName,
			Long departmentId, String departmentName, LocalDate startDate, LocalDate endDate, LocalTime morningStart,
			LocalTime morningEnd, LocalTime afternoonStart, LocalTime afternoonEnd, SpecialScheduleType type,
			String reason, SpecialScheduleStatus status, Long approverId, Long decidedBy, LocalDateTime decidedAt,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.employeeId = employeeId;
		this.employeeCode = employeeCode;
		this.employeeName = employeeName;
		this.departmentId = departmentId;
		this.departmentName = departmentName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.morningStart = morningStart;
		this.morningEnd = morningEnd;
		this.afternoonStart = afternoonStart;
		this.afternoonEnd = afternoonEnd;
		this.type = type;
		this.reason = reason;
		this.status = status;
		this.approverId = approverId;
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

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
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
    
    
    
}
