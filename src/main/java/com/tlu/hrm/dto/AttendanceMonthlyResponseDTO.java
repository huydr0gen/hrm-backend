package com.tlu.hrm.dto;

import java.util.List;

public class AttendanceMonthlyResponseDTO {

	private List<AttendanceDayResponseDTO> days;

    private int totalPaidMinutes;
    private double totalWorkingDays;
    
    private int totalOTMinutes;
    private double totalOTHours;
    
	public List<AttendanceDayResponseDTO> getDays() {
		return days;
	}
	public void setDays(List<AttendanceDayResponseDTO> days) {
		this.days = days;
	}
	public int getTotalPaidMinutes() {
		return totalPaidMinutes;
	}
	public void setTotalPaidMinutes(int totalPaidMinutes) {
		this.totalPaidMinutes = totalPaidMinutes;
	}
	public double getTotalWorkingDays() {
		return totalWorkingDays;
	}
	public void setTotalWorkingDays(double totalWorkingDays) {
		this.totalWorkingDays = totalWorkingDays;
	}
	public int getTotalOTMinutes() {
		return totalOTMinutes;
	}
	public void setTotalOTMinutes(int totalOTMinutes) {
		this.totalOTMinutes = totalOTMinutes;
	}
	public double getTotalOTHours() {
		return totalOTHours;
	}
	public void setTotalOTHours(double totalOTHours) {
		this.totalOTHours = totalOTHours;
	}
    
    
}
