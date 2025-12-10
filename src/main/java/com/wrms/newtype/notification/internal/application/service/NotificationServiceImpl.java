package com.wrms.newtype.notification.internal.application.service;

import com.wrms.newtype.notification.api.domain.NotificationType;
import com.wrms.newtype.notification.api.dto.response.NotificationResponse;
import com.wrms.newtype.notification.api.service.NotificationCommandService;
import com.wrms.newtype.notification.api.service.NotificationQueryService;
import com.wrms.newtype.notification.internal.domain.Notification;
import com.wrms.newtype.notification.internal.infrastructure.persistence.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 알림 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationCommandService, NotificationQueryService {
    
    private final NotificationRepository notificationRepository;
    
    @Override
    public NotificationResponse create(Long userId, Long counselingId, NotificationType type,
                                       String title, String message) {
        Notification notification = Notification.builder()
            .userId(userId)
            .counselingId(counselingId)
            .type(type)
            .title(title)
            .message(message)
            .build();
        
        Notification saved = notificationRepository.save(notification);
        return NotificationResponse.from(saved);
    }
    
    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + notificationId));
        notification.markAsRead();
        notificationRepository.save(notification);
    }
    
    @Override
    public void markAllAsReadByUserId(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findUnreadByUserId(userId);
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
            notificationRepository.save(notification);
        }
    }
    
    @Override
    public void delete(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
    
    @Override
    public NotificationResponse createAssignedNotification(Long userId, Long counselingId, String customerName) {
        String title = "새 상담이 할당되었습니다";
        String message = String.format("'%s' 고객의 상담이 할당되었습니다. (상담 #%d)", customerName, counselingId);
        return create(userId, counselingId, NotificationType.ASSIGNED, title, message);
    }
    
    @Override
    public NotificationResponse createTransferredNotification(Long userId, Long counselingId, String customerName, String fromUser) {
        String title = "상담이 이관되었습니다";
        String message = String.format("'%s'님으로부터 '%s' 고객의 상담이 이관되었습니다. (상담 #%d)",
            fromUser, customerName, counselingId);
        return create(userId, counselingId, NotificationType.TRANSFERRED, title, message);
    }
    
    @Override
    public NotificationResponse createEscalatedNotification(Long userId, Long counselingId, String customerName, int level) {
        String title = "상담이 에스컬레이션되었습니다";
        String message = String.format("'%s' 고객의 상담이 레벨 %d로 에스컬레이션되었습니다. (상담 #%d)",
            customerName, level, counselingId);
        return create(userId, counselingId, NotificationType.ESCALATED, title, message);
    }
    
    @Override
    public NotificationResponse createCommentAddedNotification(Long userId, Long counselingId, String customerName, String commenter) {
        String title = "새 코멘트가 추가되었습니다";
        String message = String.format("'%s'님이 '%s' 고객 상담에 코멘트를 추가했습니다. (상담 #%d)",
            commenter, customerName, counselingId);
        return create(userId, counselingId, NotificationType.COMMENT_ADDED, title, message);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> findByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(NotificationResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> findUnreadByUserId(Long userId) {
        return notificationRepository.findUnreadByUserId(userId).stream()
            .map(NotificationResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public int countUnreadByUserId(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> findRecentByUserId(Long userId, int limit) {
        return notificationRepository.findRecentByUserId(userId, limit).stream()
            .map(NotificationResponse::from)
            .collect(Collectors.toList());
    }
}

