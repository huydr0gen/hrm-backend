package com.tlu.hrm.dto;

import java.util.List;

import com.tlu.hrm.enums.DecisionAction;

public class BulkDecisionDTO {

	private List<Long> ids;
    private DecisionAction action;
    private String comment;
    
	public BulkDecisionDTO() {
		super();
	}
	
	public BulkDecisionDTO(List<Long> ids, DecisionAction action, String comment) {
		super();
		this.ids = ids;
		this.action = action;
		this.comment = comment;
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
    
    
    
}
