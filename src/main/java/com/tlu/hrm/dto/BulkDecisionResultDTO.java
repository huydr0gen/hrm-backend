package com.tlu.hrm.dto;

import java.util.List;

public class BulkDecisionResultDTO {

	private List<Long> success;
    private List<Long> failed;
    
	public BulkDecisionResultDTO() {
		super();
	}

	public BulkDecisionResultDTO(List<Long> success, List<Long> failed) {
		super();
		this.success = success;
		this.failed = failed;
	}

	public List<Long> getSuccess() {
		return success;
	}

	public void setSuccess(List<Long> success) {
		this.success = success;
	}

	public List<Long> getFailed() {
		return failed;
	}

	public void setFailed(List<Long> failed) {
		this.failed = failed;
	}
    
    
}
