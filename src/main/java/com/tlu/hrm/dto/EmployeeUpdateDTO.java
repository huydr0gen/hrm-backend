package com.tlu.hrm.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu cập nhật thông tin nhân viên")
public class EmployeeUpdateDTO {

	@Schema(
	        description = "Họ và tên nhân viên",
	        example = "Nguyễn Văn A",
	        nullable = true
	    )
	private String fullName;
	
	@Schema(
	        description = "Ngày sinh",
	        example = "1998-05-20",
	        nullable = true
	    )
    private LocalDate dateOfBirth;
	
	@Schema(
	        description = "Chức vụ",
	        example = "Trưởng phòng kinh doanh",
	        nullable = true
	    )
    private String position;
	
	@Schema(
	        description = "Phòng ban",
	        example = "Kinh doanh",
	        nullable = true
	    )
    private String department;
	
	@Schema(
	        description = "Email liên hệ",
	        example = "a.nguyen@company.com",
	        nullable = true
	    )
    private String email;
	
	@Schema(
	        description = "Số điện thoại",
	        example = "0987654321",
	        nullable = true
	    )
    private String phoneNumber;
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
        this.fullName = (fullName == null || fullName.isBlank()) ? null : fullName;
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
        this.department = (department == null || department.isBlank()) ? null : department;
    }
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
        this.email = (email == null || email.isBlank()) ? null : email;
    }
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = (phoneNumber == null || phoneNumber.isBlank()) ? null : phoneNumber;
    }
    
}
