package com.tlu.hrm.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Trạng thái xử lý của đơn xin nghỉ phép")
public enum LeaveStatus {

	@Schema(
	        description = "Đơn mới tạo, đang chờ duyệt"
	    )
	PENDING,
	
	@Schema(
	        description = "Đơn đã được duyệt"
	    )
    APPROVED,
    
    @Schema(
            description = "Đơn bị từ chối"
        )
    REJECTED
}
