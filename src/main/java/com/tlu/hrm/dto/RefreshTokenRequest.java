package com.tlu.hrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request dùng để làm mới access token")
public class RefreshTokenRequest {

	@Schema(
	        description = "Refresh token nhận được từ API login",
	        example = "eyJhbGciOiJIUzI1NiJ9.aaa.bbb"
	        )
	private String refreshToken;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	
}
