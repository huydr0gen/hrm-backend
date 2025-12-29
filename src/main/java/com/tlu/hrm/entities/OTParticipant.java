package com.tlu.hrm.entities;

import java.time.LocalDateTime;

import com.tlu.hrm.enums.OTParticipantStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "ot_participants")
public class OTParticipant {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ot_request_id")
    private OTRequest otRequest;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private OTParticipantStatus status;

    private String rejectReason;

    private LocalDateTime respondedAt;

    @PrePersist
    void prePersist() {
        status = OTParticipantStatus.PENDING;
    }

	public OTParticipant() {
		super();
	}

	public OTParticipant(Long id, OTRequest otRequest, Employee employee, OTParticipantStatus status,
			String rejectReason, LocalDateTime respondedAt) {
		super();
		this.id = id;
		this.otRequest = otRequest;
		this.employee = employee;
		this.status = status;
		this.rejectReason = rejectReason;
		this.respondedAt = respondedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OTRequest getOtRequest() {
		return otRequest;
	}

	public void setOtRequest(OTRequest otRequest) {
		this.otRequest = otRequest;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
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
