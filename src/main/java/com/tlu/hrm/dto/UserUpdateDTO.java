package com.tlu.hrm.dto;

import java.util.Set;

import com.tlu.hrm.enums.UserStatus;

public class UserUpdateDTO {
	private String password;
    private Set<String> roleNames;
    private UserStatus status; 
    
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
	public UserStatus getStatus() {
		return status;
	}
	public void setStatus(UserStatus status) {
		this.status = status;
	}
	
	

}
