package com.tlu.hrm.dto;

import java.time.LocalDate;

public class EmployeeCertificateUpdateDTO {

	private String name;
    private String issuer;
    private LocalDate issuedDate;
    private LocalDate expiredDate;
    private String note;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public LocalDate getIssuedDate() {
		return issuedDate;
	}
	public void setIssuedDate(LocalDate issuedDate) {
		this.issuedDate = issuedDate;
	}
	public LocalDate getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(LocalDate expiredDate) {
		this.expiredDate = expiredDate;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
    
    
}
