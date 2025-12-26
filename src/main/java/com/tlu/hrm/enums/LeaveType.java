package com.tlu.hrm.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Loại nghỉ phép")
public enum LeaveType {

	@Schema(description = "Nghỉ phép năm")
    ANNUAL,

    @Schema(description = "Nghỉ không lương")
    UNPAID,

    @Schema(description = "Nghỉ khác")
    OTHER
}
