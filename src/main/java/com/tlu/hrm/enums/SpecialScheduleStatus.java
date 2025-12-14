package com.tlu.hrm.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Trạng thái xử lý của lịch làm việc đặc thù")
public enum SpecialScheduleStatus {

	@Schema(
	        description = "Lịch mới tạo, đang chờ duyệt"
	    )
	PENDING,
	
	@Schema(
	        description = "Lịch đã được duyệt"
	    )
    APPROVED,
    
    @Schema(
            description = "Lịch bị từ chối"
        )
    REJECTED
}
