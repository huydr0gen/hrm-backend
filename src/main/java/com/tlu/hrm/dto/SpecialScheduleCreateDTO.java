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
        description = """
            ID nhân viên tạo lịch.
            Không bắt buộc truyền từ client.
            Hệ thống sẽ tự lấy theo tài khoản đang đăng nhập.
            """
    )
	private Long employeeId;

    // ===== Date range =====
	@Schema(
        description = """
            Ngày bắt đầu áp dụng lịch đặc thù.

            Ví dụ:
            - ON_SITE: ngày bắt đầu đi công tác
            - MATERNITY: ngày bắt đầu nghỉ thai sản
            - CHILD_CARE: ngày bắt đầu áp dụng lịch con nhỏ
            """,
        example = "2025-01-10"
    )
    private LocalDate startDate;
	
	@Schema(
        description = """
            Ngày kết thúc lịch đặc thù.

            Lưu ý nghiệp vụ:
            - Bắt buộc với ON_SITE, OTHER
            - Không cần truyền với MATERNITY, CHILD_CARE
            (Hệ thống sẽ tự tính theo quy định)
            """,
        example = "2025-01-15"
    )
    private LocalDate endDate;

    // ===== On-site time (optional) =====
	@Schema(
        description = """
            Giờ bắt đầu ca sáng.

            Chỉ áp dụng cho:
            - ON_SITE
            - CHILD_CARE
            """,
        example = "08:00"
    )
    private LocalTime morningStart;
	
	@Schema(
        description = """
            Giờ kết thúc ca sáng.

            Chỉ áp dụng cho:
            - ON_SITE
            - CHILD_CARE
            """,
        example = "12:00"
    )
    private LocalTime morningEnd;

	@Schema(
        description = """
            Giờ bắt đầu ca chiều.

            Chỉ áp dụng cho:
            - ON_SITE
            - CHILD_CARE
            """,
        example = "13:00"
    )
    private LocalTime afternoonStart;
	
	@Schema(
        description = """
            Giờ kết thúc ca chiều.

            Chỉ áp dụng cho:
            - ON_SITE
            - CHILD_CARE
            """,
        example = "17:00"
    )
    private LocalTime afternoonEnd;

    // ===== Type =====
	@Schema(
        description = """
            Loại lịch đặc thù.

            Giá trị:
            - MATERNITY: nghỉ thai sản
            - ON_SITE: đi công tác / làm việc ngoài
            - CHILD_CARE: lịch nuôi con nhỏ (7 tiếng/ngày)
            - OTHER: loại lịch đặc biệt khác
            """
    )
    private SpecialScheduleType type;

	@Schema(
        description = """
            Lý do đăng ký lịch đặc thù.
            Nhân viên mô tả ngắn gọn mục đích đăng ký.
            """,
        example = "Đi công tác khách hàng"
    )
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
