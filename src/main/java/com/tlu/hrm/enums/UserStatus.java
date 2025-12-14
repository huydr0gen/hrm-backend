package com.tlu.hrm.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Trạng thái tài khoản người dùng")
public enum UserStatus {

	@Schema(
	        description = "Tài khoản đang hoạt động, có thể đăng nhập và sử dụng hệ thống"
	    )
	ACTIVE,
	
	@Schema(
	        description = "Tài khoản bị vô hiệu hóa, không thể đăng nhập"
	    )
	INACTIVE,
	
	@Schema(
	        description = "Tài khoản bị khóa do vi phạm hoặc lý do bảo mật"
	    )
	LOCKED
}
