package com.wrms.newtype.notification.internal.domain;

import com.wrms.newtype.notification.api.domain.NotificationType;
import com.wrms.newtype.shared.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 알림 엔티티
 */
@Getter
@Table("notification")
public class Notification extends BaseEntity {
    
    @Id
    @Column("id")
    private Long id;
    
    @Column("user_id")
    private Long userId;  // 알림을 받을 사용자
    
    @Column("counseling_id")
    private Long counselingId;  // 관련 상담 (선택사항)
    
    @Column("type")
    private String type;  // ASSIGNED, TRANSFERRED, ESCALATED, etc.
    
    @Column("title")
    private String title;
    
    @Column("message")
    private String message;
    
    @Column("is_read")
    private Boolean isRead;
    
    @Column("read_at")
    private LocalDateTime readAt;
    
    /**
     * 기본 생성자 (Spring Data JDBC가 객체를 생성할 때 필요)
     */
    Notification() {
        // Spring Data JDBC가 조회 시 객체를 생성하기 위해 필요
    }
    
    @Builder
    public Notification(Long userId, Long counselingId, NotificationType type,
                       String title, String message) {
        this.userId = userId;
        this.counselingId = counselingId;
        this.type = type != null ? type.name() : null;
        this.title = title;
        this.message = message;
        this.isRead = false;
        initializeTimestamps();
    }
    
    /**
     * ID 설정 (Repository에서 사용)
     */
    void setId(Long id) {
        this.id = id;
    }
    
    /**
     * 필드 설정 (Spring Data JDBC가 조회 시 사용)
     * package-private setter로 설정
     */
    void setUserId(Long userId) {
        this.userId = userId;
    }
    
    void setCounselingId(Long counselingId) {
        this.counselingId = counselingId;
    }
    
    void setType(String type) {
        this.type = type;
    }
    
    void setTitle(String title) {
        this.title = title;
    }
    
    void setMessage(String message) {
        this.message = message;
    }
    
    void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    /**
     * 알림 타입 Enum 조회
     */
    public NotificationType getTypeEnum() {
        return type != null ? NotificationType.valueOf(type) : null;
    }
    
    /**
     * 알림 읽음 처리
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
        updateTimestamp();
    }
}

