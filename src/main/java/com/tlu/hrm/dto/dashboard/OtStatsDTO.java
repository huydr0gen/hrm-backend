package com.tlu.hrm.dto.dashboard;

public class OtStatsDTO {

	private Integer totalOtMinutes;
    private Double totalOtHours;
    private Integer otEmployees;
    
	public OtStatsDTO() {
		super();
	}
	
	public OtStatsDTO(Integer totalOtMinutes, Double totalOtHours, Integer otEmployees) {
		super();
		this.totalOtMinutes = totalOtMinutes;
		this.totalOtHours = totalOtHours;
		this.otEmployees = otEmployees;
	}
	public Integer getTotalOtMinutes() {
		return totalOtMinutes;
	}
	public void setTotalOtMinutes(Integer totalOtMinutes) {
		this.totalOtMinutes = totalOtMinutes;
	}
	public Double getTotalOtHours() {
		return totalOtHours;
	}
	public void setTotalOtHours(Double totalOtHours) {
		this.totalOtHours = totalOtHours;
	}
	public Integer getOtEmployees() {
		return otEmployees;
	}
	public void setOtEmployees(Integer otEmployees) {
		this.otEmployees = otEmployees;
	}
    
    
}
