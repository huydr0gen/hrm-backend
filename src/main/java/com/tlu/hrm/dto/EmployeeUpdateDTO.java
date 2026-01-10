package com.tlu.hrm.dto;

import java.time.LocalDate;

import com.tlu.hrm.enums.Gender;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu cập nhật thông tin nhân viên")
public class EmployeeUpdateDTO {

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
	    example = "OTHER",
	    nullable = true
	)
	private Gender gender;

	@Schema(
	    description = "Số CCCD/CMND của nhân viên (12 số, không trùng)",
	    example = "001234567890",
	    nullable = true
	)
	private String citizenId;
	
	@Schema(
	    description = "Địa chỉ",
	    example = "Hà Nội",
	    nullable = true
	)
	private String address;
	
	@Schema(
		    description = "Cấp bậc / vai trò trong tổ chức (level)",
		    example = "Quản lý",
		    nullable = true
		)
    private String position;
	
	@Schema(
	        description = "ID phòng ban",
	        example = "3"
	    )
	private Long departmentId;
	
	@Schema(
	    description = "Ngày bắt đầu đi làm thực tế",
	    example = "2026-01-15",
	    nullable = true
	)
	private LocalDate onboardDate;
	
	@Schema(
	        description = "Số điện thoại",
	        example = "0912345678"
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
	public LocalDate getOnboardDate() {
		return onboardDate;
	}
	public void setOnboardDate(LocalDate onboardDate) {
		this.onboardDate = onboardDate;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = (phoneNumber == null || phoneNumber.isBlank()) ? null : phoneNumber;
    }
    
}
