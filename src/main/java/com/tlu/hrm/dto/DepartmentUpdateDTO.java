package com.tlu.hrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu cập nhật phòng ban")
public class DepartmentUpdateDTO {

	@Schema(
	        description = "Tên phòng ban mới",
	        example = "Phòng Kế toán",
	        nullable = true
	    )
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
