package com.tlu.hrm.dto;

import java.time.LocalDate;

import com.tlu.hrm.enums.TimekeepingExplanationStatus;

public class TimekeepingExplanationFilterDTO {

	private String employeeCode;
    private String department;

    private LocalDate fromDate;
    private LocalDate toDate;

    private TimekeepingExplanationStatus status;

	public TimekeepingExplanationFilterDTO() {
		super();
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public TimekeepingExplanationStatus getStatus() {
		return status;
	}

	public void setStatus(TimekeepingExplanationStatus status) {
		this.status = status;
	}
    
    
}
