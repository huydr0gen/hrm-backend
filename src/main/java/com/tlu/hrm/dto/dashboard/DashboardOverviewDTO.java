package com.tlu.hrm.dto.dashboard;

public class DashboardOverviewDTO {

	private String month;
    private AttendanceStatsDTO attendance;
    private OtStatsDTO overtime;
    private SalaryStatsDTO salary;
    
	public DashboardOverviewDTO() {
		super();
	}
	public DashboardOverviewDTO(String month, AttendanceStatsDTO attendance, OtStatsDTO overtime,
			SalaryStatsDTO salary) {
		super();
		this.month = month;
		this.attendance = attendance;
		this.overtime = overtime;
		this.salary = salary;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public AttendanceStatsDTO getAttendance() {
		return attendance;
	}
	public void setAttendance(AttendanceStatsDTO attendance) {
		this.attendance = attendance;
	}
	public OtStatsDTO getOvertime() {
		return overtime;
	}
	public void setOvertime(OtStatsDTO overtime) {
		this.overtime = overtime;
	}
	public SalaryStatsDTO getSalary() {
		return salary;
	}
	public void setSalary(SalaryStatsDTO salary) {
		this.salary = salary;
	}
    
    
}
