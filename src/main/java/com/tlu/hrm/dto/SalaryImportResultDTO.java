package com.tlu.hrm.dto;

import java.util.ArrayList;
import java.util.List;

public class SalaryImportResultDTO {
	private int successCount;
    private int failedCount;
    private List<String> errors = new ArrayList<>();
	public int getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}
	public int getFailedCount() {
		return failedCount;
	}
	public void setFailedCount(int failedCount) {
		this.failedCount = failedCount;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
    
    
}
