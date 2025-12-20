package com.tlu.hrm.dto;

import com.tlu.hrm.enums.DecisionAction;

public class SpecialScheduleDecisionDTO {

	private DecisionAction action;
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
