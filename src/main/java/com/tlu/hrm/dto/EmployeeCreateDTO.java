package com.tlu.hrm.dto;

import java.time.LocalDate;

import com.tlu.hrm.enums.Gender;

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
	    description = "Giới tính",
	    example = "FEMALE",
	    nullable = true
	)
	private Gender gender;

	@Schema(
	    description = "Căn cước công dân",
	    example = "012345678901"
	)
	private String citizenId;

	@Schema(
	    description = "Địa chỉ thường trú",
	    example = "Hà Nội",
	    nullable = true
	)
	private String address;
	
	@Schema(
		    description = "Cấp bậc / vai trò trong tổ chức (level)",
		    example = "STAFF"
		)
    private String position;
	
	@Schema(
	        description = "Phòng ban",
	        example = "Kế toán"
	    )
	private Long departmentId;
	
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

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getCitizenId() {
		return citizenId;
	}

	public void setCitizenId(String citizenId) {
		this.citizenId = citizenId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
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
