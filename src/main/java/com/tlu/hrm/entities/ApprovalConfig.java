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

    // EMPLOYEE / DEPARTMENT
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ApprovalTargetType targetType;

    // ===== LOGIC =====
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "approver_id", nullable = false)
    private Long approverId;

    // ===== UI / UX =====
    @Column(name = "target_code", nullable = false, length = 50)
    private String targetCode;

    @Column(name = "approver_code", nullable = false, length = 50)
    private String approverCode;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

	public ApprovalConfig() {
		super();
	}
	
	public ApprovalConfig(
	        ApprovalTargetType targetType,
	        Long targetId,
	        Long approverId,
	        String targetCode,
	        String approverCode
	) {
	    this.targetType = targetType;
	    this.targetId = targetId;
	    this.approverId = approverId;
	    this.targetCode = targetCode;
	    this.approverCode = approverCode;
	    this.active = true;
	    this.createdAt = LocalDateTime.now();
	}

	public ApprovalConfig(Long id, ApprovalTargetType targetType, Long targetId, Long approverId, String targetCode,
			String approverCode, boolean active, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.targetType = targetType;
		this.targetId = targetId;
		this.approverId = approverId;
		this.targetCode = targetCode;
		this.approverCode = approverCode;
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

	public String getTargetCode() {
		return targetCode;
	}

	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}

	public String getApproverCode() {
		return approverCode;
	}

	public void setApproverCode(String approverCode) {
		this.approverCode = approverCode;
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
