package com.tlu.hrm.entities;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(
    name = "salary_records",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "month", "year"})
    }
)
public class SalaryRecord {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private Integer month;
    private Integer year;

    private Long basicSalary;
    private Long allowance;
    private Long otPay;
    private Long bonus;
    private Long deduction;

    @Column(nullable = false)
    private Long totalSalary;

    @Column(nullable = false)
    private LocalDate importedAt;

	public SalaryRecord() {
		super();
	}

	public SalaryRecord(Long id, Employee employee, Integer month, Integer year, Long basicSalary, Long allowance,
			Long otPay, Long bonus, Long deduction, Long totalSalary, LocalDate importedAt) {
		super();
		this.id = id;
		this.employee = employee;
		this.month = month;
		this.year = year;
		this.basicSalary = basicSalary;
		this.allowance = allowance;
		this.otPay = otPay;
		this.bonus = bonus;
		this.deduction = deduction;
		this.totalSalary = totalSalary;
		this.importedAt = importedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Long getBasicSalary() {
		return basicSalary;
	}

	public void setBasicSalary(Long basicSalary) {
		this.basicSalary = basicSalary;
	}

	public Long getAllowance() {
		return allowance;
	}

	public void setAllowance(Long allowance) {
		this.allowance = allowance;
	}

	public Long getOtPay() {
		return otPay;
	}

	public void setOtPay(Long otPay) {
		this.otPay = otPay;
	}

	public Long getBonus() {
		return bonus;
	}

	public void setBonus(Long bonus) {
		this.bonus = bonus;
	}

	public Long getDeduction() {
		return deduction;
	}

	public void setDeduction(Long deduction) {
		this.deduction = deduction;
	}

	public Long getTotalSalary() {
		return totalSalary;
	}

	public void setTotalSalary(Long totalSalary) {
		this.totalSalary = totalSalary;
	}

	public LocalDate getImportedAt() {
		return importedAt;
	}

	public void setImportedAt(LocalDate importedAt) {
		this.importedAt = importedAt;
	}
    
    
}
