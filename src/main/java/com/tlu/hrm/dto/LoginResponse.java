package com.tlu.hrm.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.tlu.hrm.enums.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response trả về sau khi đăng nhập hoặc refresh token")
public class LoginResponse {
	
	@Schema(
	        description = "Access token dùng để gọi các API bảo mật",
	        example = "eyJhbGciOiJIUzI1NiJ9.xxx.yyy"
	        )
	private String accessToken;
	
	@Schema(
	        description = "Refresh token dùng để lấy access token mới khi hết hạn",
	        example = "eyJhbGciOiJIUzI1NiJ9.aaa.bbb"
	        )
    private String refreshToken;
	
	@Schema(
	        description = "Username của người dùng",
	        example = "admin"
	        )
    private String username;
	
	@Schema(
	        description = "Danh sách role của người dùng",
	        example = "[\"ADMIN\", \"HR\"]"
	        )
    private Set<String> roles;
	
	@Schema(
	        description = "Trạng thái tài khoản",
	        example = "ACTIVE"
	        )
    private UserStatus status;
	
	@Schema(
	        description = "Thời điểm đăng nhập gần nhất",
	        example = "2025-12-15T10:30:00"
	        )
    private LocalDateTime lastLogin;
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Set<String> getRoles() {
		return roles;
	}
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	public UserStatus getStatus() {
		return status;
	}
	public void setStatus(UserStatus status) {
		this.status = status;
	}
	public LocalDateTime getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

}
