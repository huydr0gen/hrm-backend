package com.tlu.hrm.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "NotificationResponse",
    description = "DTO đại diện cho một thông báo hiển thị trên icon chuông"
)
public class NotificationResponse {

	@Schema(
        description = "ID của thông báo",
        example = "15"
    )
	private Long id;
	
	@Schema(
        description = "Tiêu đề ngắn của thông báo",
        example = "Giải trình công đã được duyệt"
    )
    private String title;
	
	@Schema(
        description = "Nội dung chi tiết của thông báo",
        example = "Giải trình công ngày 05/01 đã được chấp nhận"
    )
    private String content;
	
	@Schema(
        description = "Loại thông báo, xác định theo nghiệp vụ",
        example = "LEAVE_APPROVED"
    )
    private String type;
	
	@Schema(
        description = "Trạng thái đọc của thông báo (true: đã đọc, false: chưa đọc)",
        example = "false"
    )
    private Boolean isRead;
	
	@Schema(
        description = "Thời điểm tạo thông báo",
        example = "2026-01-08T10:15:30"
    )
    private LocalDateTime createdAt;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

    
}
