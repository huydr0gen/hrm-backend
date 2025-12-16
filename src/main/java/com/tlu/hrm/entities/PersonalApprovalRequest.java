package com.tlu.hrm.entities;

import java.time.LocalDateTime;

import com.tlu.hrm.enums.ApprovalStatus;
import com.tlu.hrm.enums.PersonalApprovalType;

import jakarta.persistence.*;

@Entity
@Table(name = "personal_approval_requests")
public class PersonalApprovalRequest {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	// Người tạo yêu cầu
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
	
	@Column(nullable = false)
    private String department;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonalApprovalType type;
	
	@Column(nullable = false, length = 1000)
    private String reason;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;
	
	// Người duyệt
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by")
    private User decidedBy;
	
	private LocalDateTime decidedAt;
	
	@Column(length = 1000)
    private String decisionNote;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
	public PersonalApprovalRequest() {
		super();
	}
	
	public PersonalApprovalRequest(Employee employee,
            String department,
            PersonalApprovalType type,
            String reason) {
		this.employee = employee;
		this.department = department;
		this.type = type;
		this.reason = reason;
		this.status = ApprovalStatus.PENDING;
	}
	
	public PersonalApprovalRequest(Long id, Employee employee, String department, PersonalApprovalType type,
			String reason, ApprovalStatus status, User decidedBy, LocalDateTime decidedAt, String decisionNote,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.employee = employee;
		this.department = department;
		this.type = type;
		this.reason = reason;
		this.status = status;
		this.decidedBy = decidedBy;
		this.decidedAt = decidedAt;
		this.decisionNote = decisionNote;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	@PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public PersonalApprovalType getType() {
		return type;
	}
	public void setType(PersonalApprovalType type) {
		this.type = type;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
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
