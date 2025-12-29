package com.tlu.hrm.dto;

import java.time.LocalDateTime;

import com.tlu.hrm.enums.OTParticipantStatus;

public class OTParticipantDTO {

	private Long id;
	private Long employeeId;
	private String employeeCode;
    private String employeeName;
    private OTParticipantStatus status;
    private String rejectReason;
    private LocalDateTime respondedAt;
    
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
	public OTParticipantStatus getStatus() {
		return status;
	}
	public void setStatus(OTParticipantStatus status) {
		this.status = status;
	}
	public String getRejectReason() {
		return rejectReason;
	}
	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
	public LocalDateTime getRespondedAt() {
		return respondedAt;
	}
	public void setRespondedAt(LocalDateTime respondedAt) {
		this.respondedAt = respondedAt;
	}
    
    
}
