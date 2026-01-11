package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tlu.hrm.enums.LeaveDuration;
import com.tlu.hrm.enums.LeaveStatus;
import com.tlu.hrm.enums.LeaveType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Thông tin đơn xin nghỉ phép")
public class LeaveRequestDTO {

	@Schema(description = "ID của đơn nghỉ", example = "1001")
    private Long id;

    @Schema(description = "ID của nhân viên gửi đơn", example = "5")
    private Long employeeId;

    @Schema(description = "Mã nhân viên", example = "EMP001")
    private String employeeCode;

    @Schema(description = "Tên nhân viên", example = "Nguyễn Văn A")
    private String employeeName;

    @Schema(description = "ID phòng ban", example = "3")
    private Long departmentId;

    @Schema(description = "Tên phòng ban", example = "Kinh doanh")
    private String departmentName;

    @Schema(description = "ID người được phân công duyệt", example = "12")
    private Long approverId;

    @Schema(description = "Loại nghỉ phép", example = "ANNUAL")
    private LeaveType type;
    
    @Schema(description = "Thời lượng nghỉ", example = "FULL_DAY")
    private LeaveDuration duration;

    @Schema(description = "Ngày bắt đầu nghỉ", example = "2025-12-20")
    private LocalDate startDate;

    @Schema(description = "Ngày kết thúc nghỉ", example = "2025-12-22")
    private LocalDate endDate;

    @Schema(description = "Lý do xin nghỉ", example = "Nghỉ việc gia đình")
    private String reason;

    @Schema(description = "Trạng thái đơn nghỉ", example = "PENDING")
    private LeaveStatus status;

    @Schema(description = "Ghi chú của Manager / HR", example = "Đã xem xét")
    private String managerNote;

    @Schema(description = "ID người đã duyệt", example = "3", nullable = true)
    private Long decidedBy;

    @Schema(description = "Thời điểm duyệt", example = "2025-12-18T09:30:00", nullable = true)
    private LocalDateTime decidedAt;

    @Schema(description = "Thời điểm tạo đơn")
    private LocalDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật gần nhất")
    private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Long getApproverId() {
		return approverId;
	}

	public void setApproverId(Long approverId) {
		this.approverId = approverId;
	}

	public LeaveType getType() {
		return type;
	}

	public void setType(LeaveType type) {
		this.type = type;
	}

	public LeaveDuration getDuration() {
		return duration;
	}

	public void setDuration(LeaveDuration duration) {
		this.duration = duration;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LeaveStatus getStatus() {
		return status;
	}

	public void setStatus(LeaveStatus status) {
		this.status = status;
	}

	public String getManagerNote() {
		return managerNote;
	}

	public void setManagerNote(String managerNote) {
		this.managerNote = managerNote;
	}

	public Long getDecidedBy() {
		return decidedBy;
	}

	public void setDecidedBy(Long decidedBy) {
		this.decidedBy = decidedBy;
	}

	public LocalDateTime getDecidedAt() {
		return decidedAt;
	}

	public void setDecidedAt(LocalDateTime decidedAt) {
		this.decidedAt = decidedAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
    
    
    
}
