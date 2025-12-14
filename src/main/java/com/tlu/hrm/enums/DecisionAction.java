package com.tlu.hrm.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Hành động duyệt đơn nghỉ phép")
public enum DecisionAction {

	@Schema(
	        description = "Duyệt đơn nghỉ"
	    )
	APPROVE,
	
	@Schema(
	        description = "Từ chối đơn nghỉ"
	    )
    REJECT
}
