package com.wrms.newtype.notification.api.domain;

/**
 * 알림 타입
 */
public enum NotificationType {
    ASSIGNED("새 상담이 할당되었습니다"),
    TRANSFERRED("상담이 이관되었습니다"),
    ESCALATED("상담이 에스컬레이션되었습니다"),
    STATUS_CHANGED("상담 상태가 변경되었습니다"),
    COMMENT_ADDED("새 코멘트가 추가되었습니다"),
    SLA_WARNING("SLA 기한이 임박했습니다"),
    SLA_VIOLATED("SLA 기한이 초과되었습니다"),
    RESOLVED("상담이 해결되었습니다"),
    CLOSED("상담이 종료되었습니다");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

