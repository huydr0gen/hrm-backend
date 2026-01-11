package com.tlu.hrm.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Loại nghỉ phép của nhân viên")
public enum LeaveType {

	@Schema(description = "Nghỉ phép năm (có giới hạn số ngày theo chính sách công ty)")
    ANNUAL,

    @Schema(description = "Nghỉ việc riêng có hưởng lương theo quy định")
    PERSONAL,

    @Schema(description = "Nghỉ ốm, có thể hưởng chế độ bảo hiểm xã hội nếu đủ điều kiện")
    SICK,

    @Schema(description = "Nghỉ không hưởng lương theo thỏa thuận")
    UNPAID
}
