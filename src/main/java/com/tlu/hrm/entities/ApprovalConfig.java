package com.tlu.hrm.entities;

import java.time.LocalDateTime;

import com.tlu.hrm.enums.ApprovalTargetType;

import jakarta.persistence.*;

@Entity
@Table(
    name = "approval_configs",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"target_type", "target_id"})
    }
)
public class ApprovalConfig {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ApprovalTargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;
    // employeeId hoặc departmentId

    @Column(name = "approver_id", nullable = false)
    private Long approverId;
    // userId của người duyệt

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

	public ApprovalConfig() {
		super();
	}
	
	public ApprovalConfig(
	        ApprovalTargetType targetType,
	        Long targetId,
	        Long approverId
	) {
	    this.targetType = targetType;
	    this.targetId = targetId;
	    this.approverId = approverId;
	    this.active = true;
	    this.createdAt = LocalDateTime.now();
	}

	public ApprovalConfig(Long id, ApprovalTargetType targetType, Long targetId, Long approverId, boolean active,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.targetType = targetType;
		this.targetId = targetId;
		this.approverId = approverId;
		this.active = active;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ApprovalTargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(ApprovalTargetType targetType) {
		this.targetType = targetType;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public Long getApproverId() {
		return approverId;
	}

	public void setApproverId(Long approverId) {
		this.approverId = approverId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}
