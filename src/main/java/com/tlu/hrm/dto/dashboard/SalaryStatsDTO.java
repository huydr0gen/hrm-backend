package com.tlu.hrm.dto.dashboard;

public class SalaryStatsDTO {

	private Long totalPaidSalary;
    private Long totalOtSalary;
    private Long averageSalary;
    
	public SalaryStatsDTO() {
		super();
	}
	public SalaryStatsDTO(Long totalPaidSalary, Long totalOtSalary, Long averageSalary) {
		super();
		this.totalPaidSalary = totalPaidSalary;
		this.totalOtSalary = totalOtSalary;
		this.averageSalary = averageSalary;
	}
	public Long getTotalPaidSalary() {
		return totalPaidSalary;
	}
	public void setTotalPaidSalary(Long totalPaidSalary) {
		this.totalPaidSalary = totalPaidSalary;
	}
	public Long getTotalOtSalary() {
		return totalOtSalary;
	}
	public void setTotalOtSalary(Long totalOtSalary) {
		this.totalOtSalary = totalOtSalary;
	}
	public Long getAverageSalary() {
		return averageSalary;
	}
	public void setAverageSalary(Long averageSalary) {
		this.averageSalary = averageSalary;
	}
    
    
}
