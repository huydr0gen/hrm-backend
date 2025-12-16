package com.tlu.hrm.dto;

import com.tlu.hrm.enums.DepartmentApprovalType;

public class DepartmentApprovalCreateDTO {

	private DepartmentApprovalType type;
    private String content;
    
	public DepartmentApprovalCreateDTO() {
		super();
	}
	
	public DepartmentApprovalType getType() {
		return type;
	}
	public void setType(DepartmentApprovalType type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
    
    
}
