package com.tlu.hrm.dto;

import java.util.List;

import com.tlu.hrm.enums.DecisionAction;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu duyệt / từ chối nhiều đơn nghỉ")
public class BulkDecisionDTO {

	@Schema(
	        description = "Danh sách ID đơn nghỉ",
	        example = "[1, 2, 3]"
	    )
	private List<Long> ids;
	
	@Schema(
	        description = "Hành động xử lý",
	        example = "APPROVE"
	    )
    private DecisionAction action;
	
	@Schema(
	        description = "Ghi chú chung cho các đơn",
	        example = "Duyệt hàng loạt",
	        nullable = true
	    )
    private String managerNote;
    
	public BulkDecisionDTO() {
		super();
	}
	
	public BulkDecisionDTO(List<Long> ids, DecisionAction action, String managerNote) {
		super();
		this.ids = ids;
		this.action = action;
		this.managerNote = managerNote;
	}
	
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
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
