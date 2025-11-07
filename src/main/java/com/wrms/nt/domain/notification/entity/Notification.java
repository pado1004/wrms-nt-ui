package com.wrms.nt.domain.notification.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 알림 엔티티
 */
@Table("notification")
public class Notification {

    @Id
    private Long id;

    private Long userId;  // 알림을 받을 사용자
    private Long counselingId;  // 관련 상담 (선택사항)

    private String type;  // ASSIGNED, TRANSFERRED, ESCALATED, etc.
    private String title;
    private String message;

    private Boolean isRead;
    private LocalDateTime readAt;

    private LocalDateTime createdAt;

    // Constructor
    public Notification() {
        this.isRead = false;
    }

    public Notification(Long userId, Long counselingId, String type, String title, String message) {
        this.userId = userId;
        this.counselingId = counselingId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCounselingId() {
        return counselingId;
    }

    public void setCounselingId(Long counselingId) {
        this.counselingId = counselingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NotificationType getTypeEnum() {
        return type != null ? NotificationType.valueOf(type) : null;
    }

    public void setTypeEnum(NotificationType type) {
        this.type = type != null ? type.name() : null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
