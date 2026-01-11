package com.tlu.hrm.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Thời lượng nghỉ trong ngày")
public enum LeaveDuration {

	@Schema(description = "Nghỉ cả ngày")
    FULL_DAY,

    @Schema(description = "Nghỉ nửa ngày buổi sáng")
    MORNING,

    @Schema(description = "Nghỉ nửa ngày buổi chiều")
    AFTERNOON
}
