package com.tlu.hrm.dto;

import java.util.Set;

import com.tlu.hrm.enums.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu cập nhật user")
public class UserUpdateDTO {
	
	@Schema(
	        description = "Mật khẩu mới (nếu muốn đổi)",
	        example = "newpassword123",
	        nullable = true
	    )
	private String password;
	
	@Schema(
	        description = "Danh sách role mới",
	        example = "[\"HR\", \"MANAGER\"]",
	        nullable = true
	    )
    private Set<String> roleNames;
    
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
	
	

}
