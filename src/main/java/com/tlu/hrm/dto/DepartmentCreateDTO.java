package com.tlu.hrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu tạo mới phòng ban")
public class DepartmentCreateDTO {

	@Schema(
	        description = "Tên phòng ban",
	        example = "Kế toán"
	    )
	    private String name;

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }
}
