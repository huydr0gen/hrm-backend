package com.tlu.hrm.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu tạo mới nhân viên")
public class EmployeeCreateDTO {

	@Schema(
	        description = "Mã nhân viên",
	        example = "EMP002"
	    )
	private String code;
	
	@Schema(
	        description = "Họ và tên nhân viên",
	        example = "Trần Thị B"
	    )
    private String fullName;
	
	@Schema(
	        description = "Ngày sinh",
	        example = "2000-10-15"
	    )
    private LocalDate dateOfBirth;
	
	@Schema(
	        description = "Chức vụ",
	        example = "Kế toán"
	    )
    private String position;
	
	@Schema(
	        description = "Phòng ban",
	        example = "Kế toán"
	    )
    private String department;
	
	@Schema(
	        description = "Email liên hệ",
	        example = "b.tran@company.com"
	    )
    private String email;     
	
	@Schema(
	        description = "Số điện thoại",
	        example = "0912345678"
	    )
    private String phoneNumber;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
    
    
}
