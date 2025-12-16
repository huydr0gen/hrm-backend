package com.tlu.hrm.entities;

import java.time.LocalDateTime;

import com.tlu.hrm.enums.ApprovalStatus;
import com.tlu.hrm.enums.DepartmentApprovalType;

import jakarta.persistence.*;

@Entity
@Table(name = "department_approval_requests")
public class DepartmentApprovalRequest {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Phòng ban được đề xuất
    @Column(nullable = false)
    private String department;

    // Người tạo (Manager)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentApprovalType type;

    @Column(nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    // Người duyệt (HR / ADMIN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by")
    private User decidedBy;

    private LocalDateTime decidedAt;

    @Column(length = 1000)
    private String decisionNote;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
	public DepartmentApprovalRequest() {
		super();
	}
	
	public DepartmentApprovalRequest(String department, User createdBy, 
			DepartmentApprovalType type, String content) {
		this.department = department;
		this.createdBy = createdBy;
		this.type = type;
		this.content = content;
		this.status = ApprovalStatus.PENDING;
}
	
	public DepartmentApprovalRequest(Long id, String department, User createdBy, DepartmentApprovalType type,
			String content, ApprovalStatus status, User decidedBy, LocalDateTime decidedAt, String decisionNote,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.department = department;
		this.createdBy = createdBy;
		this.type = type;
		this.content = content;
		this.status = status;
		this.decidedBy = decidedBy;
		this.decidedAt = decidedAt;
		this.decisionNote = decisionNote;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
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
	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
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
	public User getDecidedBy() {
		return decidedBy;
	}
	public void setDecidedBy(User decidedBy) {
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
