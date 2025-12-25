package com.tlu.hrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ChangePasswordDTO {

	@Schema(example = "oldPassword123")
    private String oldPassword;

    @Schema(example = "newPassword123")
    private String newPassword;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
    
    
}
