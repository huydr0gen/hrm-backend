package com.tlu.hrm.dto;

import java.util.ArrayList;
import java.util.List;

public class AttendanceImportResultDTO {

	private int totalRows;
    private int successRows;
    private List<String> errors = new ArrayList<>();
    
	public int getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	public int getSuccessRows() {
		return successRows;
	}
	public void setSuccessRows(int successRows) {
		this.successRows = successRows;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
    
    
}
