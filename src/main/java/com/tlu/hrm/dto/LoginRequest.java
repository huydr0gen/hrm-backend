package com.tlu.hrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request đăng nhập hệ thống")
public class LoginRequest {

	@Schema(
	        description = "Tên đăng nhập của người dùng",
	        example = "admin"
	        )
	private String username;
	
	@Schema(
	        description = "Mật khẩu đăng nhập",
	        example = "admin123"
	        )
	private String password;
	
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
	
	
}
