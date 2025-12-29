package com.tlu.hrm.dto;

public class OTResponseDTO {

	private boolean accept;
    private String rejectReason;
	public boolean isAccept() {
		return accept;
	}
	public void setAccept(boolean accept) {
		this.accept = accept;
	}
	public String getRejectReason() {
		return rejectReason;
	}
	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
    
    
}
