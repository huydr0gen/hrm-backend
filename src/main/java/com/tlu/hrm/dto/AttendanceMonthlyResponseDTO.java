package com.tlu.hrm.dto;

import java.util.List;

public class AttendanceMonthlyResponseDTO {

	private List<AttendanceDayResponseDTO> days;

    private int totalPaidMinutes;
    private double totalWorkingDays;
    
    private int totalOTMinutes;
    private double totalOTHours;
    
    private double annualLeaveQuota;      // Tổng số ngày phép năm nay
    private double annualLeaveUsed;       // Đã dùng
    private double annualLeaveRemaining;  // Còn lại
    
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
	public double getAnnualLeaveQuota() {
		return annualLeaveQuota;
	}
	public void setAnnualLeaveQuota(double annualLeaveQuota) {
		this.annualLeaveQuota = annualLeaveQuota;
	}
	public double getAnnualLeaveUsed() {
		return annualLeaveUsed;
	}
	public void setAnnualLeaveUsed(double annualLeaveUsed) {
		this.annualLeaveUsed = annualLeaveUsed;
	}
	public double getAnnualLeaveRemaining() {
		return annualLeaveRemaining;
	}
	public void setAnnualLeaveRemaining(double annualLeaveRemaining) {
		this.annualLeaveRemaining = annualLeaveRemaining;
	}
    
    
}
