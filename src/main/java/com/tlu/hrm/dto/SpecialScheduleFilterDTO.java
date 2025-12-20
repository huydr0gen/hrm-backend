package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.util.List;

import com.tlu.hrm.enums.SpecialScheduleStatus;

public class SpecialScheduleFilterDTO {

	// =========================
    // Employee filter
    // =========================
    private Long employeeId;                // single employee (self)
    private List<Long> employeeIds;         // multiple employees (manager)

    // =========================
    // Department filter
    // =========================
    private Long departmentId;              // filter by department

    // =========================
    // Date range filter
    // =========================
    private LocalDate dateFrom;
    private LocalDate dateTo;

    // =========================
    // Status filter
    // =========================
    private SpecialScheduleStatus status;

    // =========================
    // Paging
    // =========================
    private int page = 0;
    private int size = 10;
    
	public SpecialScheduleFilterDTO() {
		super();
	}
	
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public List<Long> getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(List<Long> employeeIds) {
		this.employeeIds = employeeIds;
	}
	public Long getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
	public LocalDate getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}
	public LocalDate getDateTo() {
		return dateTo;
	}
	public void setDateTo(LocalDate dateTo) {
		this.dateTo = dateTo;
	}
	public SpecialScheduleStatus getStatus() {
		return status;
	}
	public void setStatus(SpecialScheduleStatus status) {
		this.status = status;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
    
    
	
}
