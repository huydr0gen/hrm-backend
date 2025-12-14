package com.tlu.hrm.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Kết quả xử lý duyệt hàng loạt")
public class BulkDecisionResultDTO {

	@Schema(
	        description = "Danh sách ID xử lý thành công",
	        example = "[1, 2]"
	    )
	private List<Long> success;
	
	@Schema(
	        description = "Danh sách ID xử lý thất bại",
	        example = "[3]"
	    )
    private List<Long> failed;
    
	public BulkDecisionResultDTO() {
		super();
	}

	public BulkDecisionResultDTO(List<Long> success, List<Long> failed) {
		super();
		this.success = success;
		this.failed = failed;
	}

	public List<Long> getSuccess() {
		return success;
	}

	public void setSuccess(List<Long> success) {
		this.success = success;
	}

	public List<Long> getFailed() {
		return failed;
	}

	public void setFailed(List<Long> failed) {
		this.failed = failed;
	}
    
    
}
