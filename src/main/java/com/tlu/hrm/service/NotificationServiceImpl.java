package com.tlu.hrm.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.NotificationResponse;
import com.tlu.hrm.entities.Notification;
import com.tlu.hrm.enums.NotificationType;
import com.tlu.hrm.repository.NotificationRepository;

@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void createNotification(
            Long receiverId,
            String title,
            String content,
            NotificationType type
    ) {
        Notification notification = new Notification();
        notification.setReceiverId(receiverId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type.name());
        notification.setIsRead(false);

        // 1️⃣ Lưu DB
        Notification saved = notificationRepository.save(notification);

        // 2️⃣ PUSH REALTIME
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),          // Principal = userId
                "/queue/notifications",         // user destination
                saved                            // payload
        );
    }

    @Override
    public List<NotificationResponse> getNotificationsByReceiver(Long receiverId) {
        return notificationRepository
                .findByReceiverIdOrderByCreatedAtDesc(receiverId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public long countUnread(Long receiverId) {
        return notificationRepository
                .countByReceiverIdAndIsReadFalse(receiverId);
    }
    
    @Override
    public void markAsRead(Long notificationId, Long receiverId) {

        Notification notification = notificationRepository
                .findByIdAndReceiverId(notificationId, receiverId)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found")
                );

        if (!Boolean.TRUE.equals(notification.getIsRead())) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public void markAllAsRead(Long receiverId) {
        notificationRepository.markAllAsRead(receiverId);
    }
    
    private NotificationResponse toResponse(Notification notification) {
        NotificationResponse res = new NotificationResponse();
        res.setId(notification.getId());
        res.setTitle(notification.getTitle());
        res.setContent(notification.getContent());
        res.setType(notification.getType());
        res.setIsRead(notification.getIsRead());
        res.setCreatedAt(notification.getCreatedAt());
        return res;
    }
}
