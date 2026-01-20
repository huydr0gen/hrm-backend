package com.tlu.hrm.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.tlu.hrm.enums.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Thông tin user trả về cho frontend")
public class UserDTO {

	@Schema(
	        description = "ID của user",
	        example = "1"
	    )
	private Long id;
	
	@Schema(
	        description = "Tên đăng nhập",
	        example = "admin"
	    )
    private String username;
	
	@Schema(
	        description = "Danh sách role của user",
	        example = "[\"ADMIN\", \"HR\"]"
	    )
    private Set<String> roles;
	
	@Schema(
	        description = "Trạng thái tài khoản",
	        example = "ACTIVE"
	    )
    private UserStatus status;
	
	@Schema(
	        description = "ID employee liên kết (nếu có)",
	        example = "10"
	    )
    private Long employeeId;
	
	@Schema(
	    description = "Mã nhân viên",
	    example = "NV001"
	)
	private String empCode;
	
	@Schema(
	    description = "Mã nhân viên của người duyệt (ưu tiên cá nhân, fallback phòng ban)",
	    example = "NV005"
	)
	private String approveCode;
	
	@Schema(
	        description = "Thời điểm tạo user",
	        example = "2025-12-01T09:00:00"
	    )
    private LocalDateTime createdAt;
	
	@Schema(
	        description = "Thời điểm cập nhật gần nhất",
	        example = "2025-12-10T14:30:00"
	    )
    private LocalDateTime updatedAt;
	
	@Schema(
	        description = "Thời điểm đăng nhập gần nhất",
	        example = "2025-12-15T10:15:00"
	    )
    private LocalDateTime lastLogin;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmpCode() {
		return empCode;
	}
	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}
	public String getApproveCode() {
		return approveCode;
	}
	public void setApproveCode(String approveCode) {
		this.approveCode = approveCode;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	public LocalDateTime getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	
	
}
