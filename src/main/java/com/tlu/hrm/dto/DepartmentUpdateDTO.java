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
	
	@Schema(
	    description = "Mô tả phòng ban",
	    example = "Phòng phụ trách tài chính",
	    nullable = true
	)
	private String description;

    @Schema(
        description = "Trạng thái hoạt động của phòng ban",
        example = "true",
        nullable = true
    )
    private Boolean active;

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

	public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
