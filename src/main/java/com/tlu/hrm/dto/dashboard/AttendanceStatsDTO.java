package com.tlu.hrm.dto.dashboard;

public class AttendanceStatsDTO {

	private Integer totalWorkingDays;
    private Integer lateCount;
    private Integer lateEmployees;
    private Double lateRate;
    
	public AttendanceStatsDTO() {
		super();
	}
	public AttendanceStatsDTO(Integer totalWorkingDays, Integer lateCount, Integer lateEmployees, Double lateRate) {
		super();
		this.totalWorkingDays = totalWorkingDays;
		this.lateCount = lateCount;
		this.lateEmployees = lateEmployees;
		this.lateRate = lateRate;
	}
	public Integer getTotalWorkingDays() {
		return totalWorkingDays;
	}
	public void setTotalWorkingDays(Integer totalWorkingDays) {
		this.totalWorkingDays = totalWorkingDays;
	}
	public Integer getLateCount() {
		return lateCount;
	}
	public void setLateCount(Integer lateCount) {
		this.lateCount = lateCount;
	}
	public Integer getLateEmployees() {
		return lateEmployees;
	}
	public void setLateEmployees(Integer lateEmployees) {
		this.lateEmployees = lateEmployees;
	}
	public Double getLateRate() {
		return lateRate;
	}
	public void setLateRate(Double lateRate) {
		this.lateRate = lateRate;
	}
    
    
}
