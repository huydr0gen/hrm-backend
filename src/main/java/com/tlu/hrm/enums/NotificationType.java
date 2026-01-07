package com.tlu.hrm.enums;

public enum NotificationType {

	/* ================= NGHỈ PHÉP ================= */
    LEAVE_REQUEST,
    LEAVE_APPROVED,
    LEAVE_REJECTED,

    /* ================= GIẢI TRÌNH CÔNG ================= */
    EXPLANATION_REQUEST,
    EXPLANATION_APPROVED,
    EXPLANATION_REJECTED,

    /* ================= LỊCH ĐẶC THÙ ================= */
    SPECIAL_SCHEDULE_REQUEST,     // Tạo lịch đặc thù
    SPECIAL_SCHEDULE_APPROVED,    // Duyệt lịch đặc thù
    SPECIAL_SCHEDULE_REJECTED,    // Từ chối lịch đặc thù

    /* ================= LÀM THÊM GIỜ (OT) ================= */
    OT_REQUEST,
    OT_APPROVED,
    OT_REJECTED,

    /* ================= HỆ THỐNG ================= */
    SYSTEM
}
