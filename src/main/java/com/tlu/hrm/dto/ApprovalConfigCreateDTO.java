package com.tlu.hrm.dto;

import com.tlu.hrm.enums.ApprovalTargetType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "ApprovalConfigCreateDTO",
    description = """
        Dữ liệu tạo hoặc cập nhật cấu hình người duyệt.

        Quy ước:
        - Nếu id = null → tạo mới cấu hình
        - Nếu id != null → cập nhật cấu hình tương ứng

        Ràng buộc nghiệp vụ:
        - Mỗi nhân viên hoặc phòng ban chỉ được có 1 người duyệt
        - Nếu targetType = EMPLOYEE:
            + Nếu người được duyệt là MANAGER → người duyệt phải có cả role MANAGER và HR
            + Nếu người được duyệt là EMPLOYEE thường → người duyệt chỉ cần MANAGER hoặc HR
        """
)
public class ApprovalConfigCreateDTO {

	@Schema(
        description = """
            ID cấu hình người duyệt.

            - null → tạo mới
            - có giá trị → cập nhật cấu hình tương ứng
            """,
        example = "10",
        nullable = true
    )
    private Long id;

	@Schema(
        description = """
            Loại đối tượng được thiết lập người duyệt.

            - EMPLOYEE: thiết lập cho cá nhân
            - DEPARTMENT: thiết lập cho phòng ban
            """,
        example = "EMPLOYEE",
        allowableValues = {"EMPLOYEE", "DEPARTMENT"},
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ApprovalTargetType targetType;

	@Schema(
        description = """
            Mã đối tượng được áp dụng.

            - Nếu targetType = EMPLOYEE → employeeCode
            - Nếu targetType = DEPARTMENT → departmentCode
            """,
        example = "EMP005 (nếu EMPLOYEE) hoặc IT (nếu DEPARTMENT)",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String targetCode;

	@Schema(
        description = """
            Mã nhân viên của người duyệt (employeeCode).

            Ràng buộc:
            - Nếu duyệt cho MANAGER → người duyệt phải có cả role MANAGER và HR
            - Nếu duyệt cho EMPLOYEE thường → người duyệt chỉ cần MANAGER hoặc HR
            """,
        example = "EMP003",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String approverCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ApprovalTargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(ApprovalTargetType targetType) {
		this.targetType = targetType;
	}

	public String getTargetCode() {
		return targetCode;
	}

	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}

	public String getApproverCode() {
		return approverCode;
	}

	public void setApproverCode(String approverCode) {
		this.approverCode = approverCode;
	}
    
    
}
