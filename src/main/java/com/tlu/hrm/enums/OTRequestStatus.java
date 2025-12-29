package com.tlu.hrm.enums;

public enum OTRequestStatus {

	PENDING,              // chờ nhân viên phản hồi
    PARTIALLY_ACCEPTED,   // có người đồng ý, có người chưa
    ACCEPTED,             // tất cả đồng ý
    REJECTED,             // tất cả từ chối
    COMPLETED,            // đã cộng OT
    CANCELLED             // manager huỷ
}
