package com.tlu.hrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dữ liệu tạo mới phòng ban")
public class DepartmentCreateDTO {

	@Schema(
        description = "Tên phòng ban",
        example = "Kế toán"
    )
    private String name;
	
	@Schema(
	    description = "Mô tả phòng ban",
	    example = "Phòng phụ trách các nghiệp vụ tài chính và kế toán",
	    nullable = true
	)
	private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
    
}
