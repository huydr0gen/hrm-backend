package com.tlu.hrm.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.tlu.hrm.enums.UserStatus;

public class LoginResponse {
	private String token;
    private String username;
    private Set<String> roles;
    private UserStatus status;
    private LocalDateTime lastLogin;
    
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
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
