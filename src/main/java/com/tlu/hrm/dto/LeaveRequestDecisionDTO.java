package com.tlu.hrm.dto;

import com.tlu.hrm.enums.DecisionAction;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu duyệt hoặc từ chối đơn nghỉ")
public class LeaveRequestDecisionDTO {

	// APPROVE hoặc REJECT
	@Schema(
        description = "Hành động xử lý đơn",
        example = "APPROVE"
    )
    private DecisionAction action;

    // Ghi chú của HR/Manager
	@Schema(
        description = "Ghi chú của Manager / HR",
        example = "Đồng ý cho nghỉ"
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
