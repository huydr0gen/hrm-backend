package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tlu.hrm.enums.EmployeeStatus;
import com.tlu.hrm.enums.Gender;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Thông tin nhân viên trả về cho frontend")
public class EmployeeDTO {

	@Schema(
	        description = "ID của nhân viên",
	        example = "1"
	    )
	    private Long id;

	    @Schema(
	        description = "Mã nhân viên",
	        example = "EMP001"
	    )
	    private String code;

	    @Schema(
	        description = "Họ và tên nhân viên",
	        example = "Nguyễn Văn A"
	    )
	    private String fullName;

	    @Schema(
	        description = "Ngày sinh",
	        example = "1998-05-20"
	    )
	    private LocalDate dateOfBirth;
	    
	    @Schema(
    	    description = "Giới tính",
    	    example = "MALE"
    	)
    	private Gender gender;

    	@Schema(
    	    description = "Căn cước công dân"
    	)
    	private String citizenId;

    	@Schema(
    	    description = "Địa chỉ"
    	)
    	private String address;

	    @Schema(
	    	    description = "Cấp bậc / vai trò trong tổ chức (level)",
	    	    example = "MANAGER"
	    	)
	    private String position;

	    @Schema(
	        description = "ID phòng ban",
	        example = "3"
	    )
	    private Long departmentId;

	    @Schema(
	        description = "Tên phòng ban",
	        example = "Kinh doanh"
	    )
	    private String departmentName;
	    
	    @Schema(
		        description = "Trạng thái của nhân viên",
		        example = "ACTIVE"
		    )
		    private EmployeeStatus status;

	    @Schema(
	        description = "Email liên hệ",
	        example = "a.nguyen@company.com"
	    )
	    private String email;

	    @Schema(
	        description = "Số điện thoại",
	        example = "0987654321"
	    )
	    private String phoneNumber;

	    @Schema(
	        description = "ID user gắn với employee (nếu đã có tài khoản)",
	        example = "5",
	        nullable = true
	    )
	    private Long userId;
	    
	    @Schema(
            description = "Thời điểm tạo nhân viên",
            example = "2025-12-26T10:15:30"
        )
        private LocalDateTime createdAt;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

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

		public String getDepartmentName() {
			return departmentName;
		}

		public void setDepartmentName(String departmentName) {
			this.departmentName = departmentName;
		}

		public EmployeeStatus getStatus() {
			return status;
		}

		public void setStatus(EmployeeStatus status) {
			this.status = status;
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

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}


		
	    
}
