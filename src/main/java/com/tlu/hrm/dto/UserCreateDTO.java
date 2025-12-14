package com.tlu.hrm.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu tạo user mới")
public class UserCreateDTO {
	
	@Schema(
	        description = "Username đăng nhập",
	        example = "employee01"
	    )
	private String username;
	
	@Schema(
	        description = "Mật khẩu khởi tạo",
	        example = "123456"
	    )
	private String password;
	
	@Schema(
	        description = "Danh sách role gán cho user",
	        example = "[\"EMPLOYEE\"]"
	    )
	private Set<String> roleNames;
	
	@Schema(
	        description = "ID employee để liên kết (nếu tạo user cho employee có sẵn)",
	        example = "5",
	        nullable = true
	    )
	private Long employeeId;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Set<String> getRoleNames() {
		return roleNames;
	}
	public void setRoleNames(Set<String> roleNames) {
		this.roleNames = roleNames;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
}
