package com.tlu.hrm.dto;

import com.tlu.hrm.enums.DecisionAction;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "DTO dùng cho hành động duyệt hoặc từ chối lịch làm việc đặc thù"
)
public class SpecialScheduleDecisionDTO {

	@Schema(
        description = """
            Hành động duyệt lịch đặc thù.

            Giá trị:
            - APPROVE: duyệt lịch
            - REJECT: từ chối lịch

            Lưu ý:
            - Chỉ người được thiết lập là approver mới được thực hiện
            - Chỉ áp dụng khi lịch đang ở trạng thái PENDING
            """
    )
	private DecisionAction action;
	
	@Schema(
        description = """
            Ghi chú của người duyệt.

            Ghi chú:
            - Hiện tại chưa được lưu xuống database
            - Dùng để mở rộng trong tương lai (ví dụ: lưu lý do từ chối)
            """,
        example = "Không đủ căn cứ phê duyệt"
    )
	private String managerNote;
	
	public DecisionAction getAction() {
		return action;
	}
	public void setAction(DecisionAction action) {
		this.action = action;
	}
	public String getManagerNote() {
		return managerNote;
	}
	public void setManagerNote(String managerNote) {
		this.managerNote = managerNote;
	}
    
	
}
