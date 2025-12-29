package com.tlu.hrm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.tlu.hrm.enums.OTRequestStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "ot_requests")
public class OTRequest {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate otDate;

    private LocalTime startTime;
    private LocalTime endTime;

    private String reason;

    @Enumerated(EnumType.STRING)
    private OTRequestStatus status;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @OneToMany(mappedBy = "otRequest", cascade = CascadeType.ALL)
    private List<OTParticipant> participants = new ArrayList<>();

    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        status = OTRequestStatus.PENDING;
    }

	public OTRequest() {
		super();
	}

	public OTRequest(Long id, LocalDate otDate, LocalTime startTime, LocalTime endTime, String reason,
			OTRequestStatus status, Employee manager, List<OTParticipant> participants, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.otDate = otDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.reason = reason;
		this.status = status;
		this.manager = manager;
		this.participants = participants;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getOtDate() {
		return otDate;
	}

	public void setOtDate(LocalDate otDate) {
		this.otDate = otDate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public OTRequestStatus getStatus() {
		return status;
	}

	public void setStatus(OTRequestStatus status) {
		this.status = status;
	}

	public Employee getManager() {
		return manager;
	}

	public void setManager(Employee manager) {
		this.manager = manager;
	}

	public List<OTParticipant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<OTParticipant> participants) {
		this.participants = participants;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}
