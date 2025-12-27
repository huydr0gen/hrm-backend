package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tlu.hrm.enums.SpecialScheduleType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "DTO dùng cho nhân viên tạo lịch làm việc đặc thù"
)
public class SpecialScheduleCreateDTO {

	@Schema(
	        description = "ID nhân viên tạo lịch. Hệ thống tự lấy theo tài khoản đăng nhập."
	    )
	    private Long employeeId;

	    // =========================
	    // Date range
	    // =========================
	    @Schema(
	        description = "Ngày bắt đầu áp dụng lịch đặc thù",
	        example = "2025-01-10",
	        required = true
	    )
	    private LocalDate startDate;

	    @Schema(
	        description = """
	            Ngày kết thúc lịch đặc thù.
	            - Bắt buộc với ON_SITE, OTHER
	            - MATERNITY, CHILD_CARE hệ thống tự tính
	            """,
	        example = "2025-01-15"
	    )
	    private LocalDate endDate;

	    // =========================
	    // Working time
	    // =========================
	    private LocalTime morningStart;
	    private LocalTime morningEnd;
	    private LocalTime afternoonStart;
	    private LocalTime afternoonEnd;

	    // =========================
	    // ON_SITE project info
	    // =========================
	    @Schema(description = "Mã dự án (bắt buộc với ON_SITE)", example = "PRJ-001")
	    private String projectCode;

	    @Schema(description = "Tên dự án (bắt buộc với ON_SITE)", example = "Dự án ERP")
	    private String projectName;

	    @Schema(description = "Mã nhân viên của quản lý dự án", example = "EMP005")
	    private String managerCode;

	    @Schema(description = "Tên quản lý dự án", example = "Nguyễn Văn A")
	    private String managerName;

	    // =========================
	    // Type & reason
	    // =========================
	    @Schema(
	        description = """
	            Loại lịch đặc thù:
	            - MATERNITY
	            - ON_SITE
	            - CHILD_CARE
	            - OTHER
	            """,
	        required = true
	    )
	    private SpecialScheduleType type;

	    private String reason;

		public SpecialScheduleCreateDTO() {
			super();
		}

		public Long getEmployeeId() {
			return employeeId;
		}

		public void setEmployeeId(Long employeeId) {
			this.employeeId = employeeId;
		}

		public LocalDate getStartDate() {
			return startDate;
		}

		public void setStartDate(LocalDate startDate) {
			this.startDate = startDate;
		}

		public LocalDate getEndDate() {
			return endDate;
		}

		public void setEndDate(LocalDate endDate) {
			this.endDate = endDate;
		}

		public LocalTime getMorningStart() {
			return morningStart;
		}

		public void setMorningStart(LocalTime morningStart) {
			this.morningStart = morningStart;
		}

		public LocalTime getMorningEnd() {
			return morningEnd;
		}

		public void setMorningEnd(LocalTime morningEnd) {
			this.morningEnd = morningEnd;
		}

		public LocalTime getAfternoonStart() {
			return afternoonStart;
		}

		public void setAfternoonStart(LocalTime afternoonStart) {
			this.afternoonStart = afternoonStart;
		}

		public LocalTime getAfternoonEnd() {
			return afternoonEnd;
		}

		public void setAfternoonEnd(LocalTime afternoonEnd) {
			this.afternoonEnd = afternoonEnd;
		}

		public String getProjectCode() {
			return projectCode;
		}

		public void setProjectCode(String projectCode) {
			this.projectCode = projectCode;
		}

		public String getProjectName() {
			return projectName;
		}

		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}

		public String getManagerCode() {
			return managerCode;
		}

		public void setManagerCode(String managerCode) {
			this.managerCode = managerCode;
		}

		public String getManagerName() {
			return managerName;
		}

		public void setManagerName(String managerName) {
			this.managerName = managerName;
		}

		public SpecialScheduleType getType() {
			return type;
		}

		public void setType(SpecialScheduleType type) {
			this.type = type;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}
	    
	    
	
}
