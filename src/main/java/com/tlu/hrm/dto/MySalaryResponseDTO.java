package com.tlu.hrm.dto;

import com.tlu.hrm.entities.SalaryRecord;

public class MySalaryResponseDTO {

	private String status; // AVAILABLE | NOT_AVAILABLE
    private Integer month;
    private Integer year;

    private SalaryDetailDTO salary;
    private String message;

    public static MySalaryResponseDTO notAvailable(int month, int year) {
        MySalaryResponseDTO dto = new MySalaryResponseDTO();
        dto.status = "NOT_AVAILABLE";
        dto.month = month;
        dto.year = year;
        dto.message = "Chưa có bảng lương cho tháng này";
        return dto;
    }

    public static MySalaryResponseDTO available(SalaryRecord r) {
        MySalaryResponseDTO dto = new MySalaryResponseDTO();
        dto.status = "AVAILABLE";
        dto.month = r.getMonth();
        dto.year = r.getYear();

        SalaryDetailDTO d = new SalaryDetailDTO();
        d.setBasic(r.getBasicSalary());
        d.setAllowance(r.getAllowance());
        d.setOtPay(r.getOtPay());
        d.setBonus(r.getBonus());
        d.setDeduction(r.getDeduction());
        d.setTotal(r.getTotalSalary());

        dto.salary = d;
        return dto;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public SalaryDetailDTO getSalary() {
		return salary;
	}

	public void setSalary(SalaryDetailDTO salary) {
		this.salary = salary;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
    
}
