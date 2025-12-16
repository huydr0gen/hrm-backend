package com.tlu.hrm.dto;

import java.time.LocalDateTime;

import com.tlu.hrm.enums.ApprovalStatus;
import com.tlu.hrm.enums.DepartmentApprovalType;

public class DepartmentApprovalResponseDTO {

	private Long id;

    private String department;

    private DepartmentApprovalType type;
    private String content;

    private ApprovalStatus status;

    private String createdBy;
    private LocalDateTime createdAt;

    private String decidedBy;
    private LocalDateTime decidedAt;
    private String decisionNote;
    
	public DepartmentApprovalResponseDTO() {
		super();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public DepartmentApprovalType getType() {
		return type;
	}
	public void setType(DepartmentApprovalType type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public ApprovalStatus getStatus() {
		return status;
	}
	public void setStatus(ApprovalStatus status) {
		this.status = status;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getDecidedBy() {
		return decidedBy;
	}
	public void setDecidedBy(String decidedBy) {
		this.decidedBy = decidedBy;
	}
	public LocalDateTime getDecidedAt() {
		return decidedAt;
	}
	public void setDecidedAt(LocalDateTime decidedAt) {
		this.decidedAt = decidedAt;
	}
	public String getDecisionNote() {
		return decisionNote;
	}
	public void setDecisionNote(String decisionNote) {
		this.decisionNote = decisionNote;
	}
    
    
}
