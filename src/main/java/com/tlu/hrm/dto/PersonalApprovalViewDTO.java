package com.tlu.hrm.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "PersonalApprovalViewDTO",
    description = """
        Thông tin cấu hình người duyệt cho cá nhân nhân viên.

        Dữ liệu trả về đã được xử lý để phục vụ hiển thị UI,
        bao gồm cả thông tin logic và thông tin hiển thị.
        """
)
public class PersonalApprovalViewDTO {

	@Schema(
	        description = "ID nhân viên được áp dụng cấu hình duyệt",
	        example = "10"
	    )
	    private Long employeeId;

	    @Schema(
	        description = "Mã nhân viên",
	        example = "EMP010"
	    )
	    private String employeeCode;

	    @Schema(
	        description = "Tên nhân viên",
	        example = "Nguyễn Văn B"
	    )
	    private String employeeName;

	    @Schema(
            description = """
                Chuỗi hiển thị nhân viên được duyệt.

                Format:
                username - EMPxxx - Full Name

                Ví dụ:
                quanglm - EMP010 - Nguyễn Văn B
                """,
            example = "quanglm - EMP010 - Nguyễn Văn B"
        )
	    private String employeeDisplay;

	    @Schema(
	        description = "ID của người duyệt",
	        example = "3"
	    )
	    private Long approverId;

	    @Schema(
	        description = "Mã nhân viên của người duyệt",
	        example = "EMP003"
	    )
	    private String approverCode;

	    @Schema(
	        description = "Tên nhân viên của người duyệt",
	        example = "Nguyễn Văn A"
	    )
	    private String approverName;
	    
	    @Schema(
            description = """
                Chuỗi hiển thị người duyệt.

                Format:
                username - EMPxxx - Full Name

                Ví dụ:
                quanglm - EMP003 - Nguyễn Văn A
                """,
            example = "quanglm - EMP003 - Nguyễn Văn A"
        )
	    private String approverDisplay;

	    @Schema(
	        description = "Thời điểm thiết lập hoặc cập nhật người duyệt",
	        example = "2025-12-21T10:15:00"
	    )
	    private LocalDateTime createdAt;

		public Long getEmployeeId() {
			return employeeId;
		}

		public void setEmployeeId(Long employeeId) {
			this.employeeId = employeeId;
		}

		public String getEmployeeCode() {
			return employeeCode;
		}

		public void setEmployeeCode(String employeeCode) {
			this.employeeCode = employeeCode;
		}

		public String getEmployeeName() {
			return employeeName;
		}

		public void setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
		}

		public Long getApproverId() {
			return approverId;
		}

		public void setApproverId(Long approverId) {
			this.approverId = approverId;
		}

		public String getApproverCode() {
			return approverCode;
		}

		public void setApproverCode(String approverCode) {
			this.approverCode = approverCode;
		}

		public String getApproverName() {
			return approverName;
		}

		public void setApproverName(String approverName) {
			this.approverName = approverName;
		}

		public String getEmployeeDisplay() {
			return employeeDisplay;
		}

		public void setEmployeeDisplay(String employeeDisplay) {
			this.employeeDisplay = employeeDisplay;
		}

		public String getApproverDisplay() {
			return approverDisplay;
		}

		public void setApproverDisplay(String approverDisplay) {
			this.approverDisplay = approverDisplay;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
	    
	    
    
}
