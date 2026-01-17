package com.tlu.hrm.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "EligibleApproverDTO",
    description = "Thông tin người có thể được chọn làm người duyệt"
)
public class EligibleApproverDTO {

	@Schema(description = "ID của nhân viên", example = "5")
    private Long employeeId;

    @Schema(description = "Mã nhân viên", example = "EMP005")
    private String employeeCode;

    @Schema(description = "Tên đầy đủ của nhân viên", example = "Lê Minh Quang")
    private String fullName;

    @Schema(description = "Username đăng nhập", example = "quanglm")
    private String username;

    @Schema(
        description = "Danh sách role của user",
        example = "[\"EMPLOYEE\", \"MANAGER\", \"HR\"]"
    )
    private List<String> roles;

    @Schema(
        description = "Chuỗi hiển thị UI: username - EMPxxx - Full Name",
        example = "quanglm - EMP005 - Lê Minh Quang"
    )
    private String display;

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
    
}
