package com.wrms.newtype.notification.api.service;

import com.wrms.newtype.notification.api.domain.NotificationType;
import com.wrms.newtype.notification.api.dto.response.NotificationResponse;

/**
 * 알림 Command 서비스 인터페이스
 */
public interface NotificationCommandService {
    
    /**
     * 알림 생성
     */
    NotificationResponse create(Long userId, Long counselingId, NotificationType type,
                                String title, String message);
    
    /**
     * 알림 읽음 처리
     */
    void markAsRead(Long notificationId);
    
    /**
     * 사용자의 모든 알림 읽음 처리
     */
    void markAllAsReadByUserId(Long userId);
    
    /**
     * 알림 삭제
     */
    void delete(Long notificationId);
    
    /**
     * 상담 할당 알림 생성
     */
    NotificationResponse createAssignedNotification(Long userId, Long counselingId, String customerName);
    
    /**
     * 상담 이관 알림 생성
     */
    NotificationResponse createTransferredNotification(Long userId, Long counselingId, String customerName, String fromUser);
    
    /**
     * 에스컬레이션 알림 생성
     */
    NotificationResponse createEscalatedNotification(Long userId, Long counselingId, String customerName, int level);
    
    /**
     * 코멘트 추가 알림 생성
     */
    NotificationResponse createCommentAddedNotification(Long userId, Long counselingId, String customerName, String commenter);
}

