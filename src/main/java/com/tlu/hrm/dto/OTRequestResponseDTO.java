package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.tlu.hrm.enums.OTRequestStatus;

public class OTRequestResponseDTO {

	private Long id;
	private Long managerId;
	private String managerName;
    private LocalDate otDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
    private OTRequestStatus status;
    private List<OTParticipantDTO> participants;
    private LocalDateTime createdAt;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getManagerId() {
		return managerId;
	}
	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
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
	public List<OTParticipantDTO> getParticipants() {
		return participants;
	}
	public void setParticipants(List<OTParticipantDTO> participants) {
		this.participants = participants;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}
