package com.tlu.hrm.dto;

import com.tlu.hrm.enums.DecisionAction;

public class SpecialScheduleDecisionDTO {

	private DecisionAction action;
    
	public DecisionAction getAction() {
		return action;
	}
	public void setAction(DecisionAction action) {
		this.action = action;
	}
    
}
