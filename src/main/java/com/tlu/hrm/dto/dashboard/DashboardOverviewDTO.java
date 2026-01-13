package com.tlu.hrm.dto.dashboard;

public class DashboardOverviewDTO {

	private String month;

    // ===== Employee stats =====
    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
    private long lockedEmployees;

    // ===== Department stats =====
    private long totalDepartments;
    private long activeDepartments;
    private long inactiveDepartments;

    // ===== Existing stats =====
    private AttendanceStatsDTO attendance;
    private OtStatsDTO overtime;
    private SalaryStatsDTO salary;
    
	public DashboardOverviewDTO() {
		super();
	}
	
	public DashboardOverviewDTO(String month, long totalEmployees, long activeEmployees, long inactiveEmployees,
			long lockedEmployees, long totalDepartments, AttendanceStatsDTO attendance, OtStatsDTO overtime,
			SalaryStatsDTO salary) {
		super();
		this.month = month;
		this.totalEmployees = totalEmployees;
		this.activeEmployees = activeEmployees;
		this.inactiveEmployees = inactiveEmployees;
		this.lockedEmployees = lockedEmployees;
		this.totalDepartments = totalDepartments;
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

	public long getTotalEmployees() {
		return totalEmployees;
	}

	public void setTotalEmployees(long totalEmployees) {
		this.totalEmployees = totalEmployees;
	}

	public long getActiveEmployees() {
		return activeEmployees;
	}

	public void setActiveEmployees(long activeEmployees) {
		this.activeEmployees = activeEmployees;
	}

	public long getInactiveEmployees() {
		return inactiveEmployees;
	}

	public void setInactiveEmployees(long inactiveEmployees) {
		this.inactiveEmployees = inactiveEmployees;
	}

	public long getLockedEmployees() {
		return lockedEmployees;
	}

	public void setLockedEmployees(long lockedEmployees) {
		this.lockedEmployees = lockedEmployees;
	}

	public long getTotalDepartments() {
		return totalDepartments;
	}

	public void setTotalDepartments(long totalDepartments) {
		this.totalDepartments = totalDepartments;
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
