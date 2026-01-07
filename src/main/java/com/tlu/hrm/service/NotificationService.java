package com.tlu.hrm.service;

import java.util.List;

import com.tlu.hrm.dto.NotificationResponse;
import com.tlu.hrm.enums.NotificationType;

public interface NotificationService {

	void createNotification(Long receiverId, String title, String content, NotificationType type);

	List<NotificationResponse> getNotificationsByReceiver(Long receiverId);

    long countUnread(Long receiverId);

    void markAsRead(Long notificationId, Long receiverId);
    
    void markAllAsRead(Long receiverId);
}
