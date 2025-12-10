package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.util.List;

import com.tlu.hrm.enums.SpecialScheduleStatus;

public class SpecialScheduleFilterDTO {

	private Long employeeId;                // single employee
    private List<Long> employeeIds;         // multiple employees (for manager)
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private SpecialScheduleStatus status;

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
