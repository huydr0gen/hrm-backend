package com.tlu.hrm.dto;

public class SalaryDetailDTO {

	private Long basic;
    private Long allowance;
    private Long otPay;
    private Long bonus;
    private Long deduction;
    private Long total;
	public Long getBasic() {
		return basic;
	}
	public void setBasic(Long basic) {
		this.basic = basic;
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
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
    
    
}
