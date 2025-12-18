package com.tlu.hrm.dto;

import com.tlu.hrm.enums.DecisionAction;

import jakarta.validation.constraints.NotNull;

public class TimekeepingExplanationDecisionDTO {

	@NotNull
    private DecisionAction action; // APPROVE / REJECT

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
