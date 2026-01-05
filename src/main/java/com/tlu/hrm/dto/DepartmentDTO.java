package com.tlu.hrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Thông tin phòng ban trả về cho frontend")
public class DepartmentDTO {

	@Schema(
	        description = "ID phòng ban",
	        example = "1"
	)
    private Long id;
	
	@Schema(
	        description = "Mã phòng ban",
	        example = "DEP001"
	    )
	    private String code;

    @Schema(
        description = "Tên phòng ban",
        example = "Kế toán"
    )
    private String name;
    
    @Schema(
	    description = "Mô tả phòng ban"
	)
	private String description;

    @Schema(
        description = "Trạng thái hoạt động",
        example = "true"
    )
    private boolean active;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
    
    
}
