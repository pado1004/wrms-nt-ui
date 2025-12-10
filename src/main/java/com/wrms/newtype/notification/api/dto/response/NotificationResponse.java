package com.wrms.newtype.notification.api.dto.response;

import com.wrms.newtype.notification.api.domain.NotificationType;

import java.time.LocalDateTime;

/**
 * 알림 응답 DTO
 */
public record NotificationResponse(
    Long id,
    Long userId,
    Long counselingId,
    NotificationType type,
    String title,
    String message,
    Boolean isRead,
    LocalDateTime readAt,
    LocalDateTime createdAt
) {
    /**
     * 도메인 엔티티로부터 Response DTO를 생성합니다.
     */
    public static NotificationResponse from(com.wrms.newtype.notification.internal.domain.Notification entity) {
        return new NotificationResponse(
            entity.getId(),
            entity.getUserId(),
            entity.getCounselingId(),
            entity.getTypeEnum(),
            entity.getTitle(),
            entity.getMessage(),
            entity.getIsRead(),
            entity.getReadAt(),
            entity.getCreatedAt()
        );
    }
}

