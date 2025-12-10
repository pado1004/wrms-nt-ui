package com.wrms.newtype.counseling.internal.domain;

import com.wrms.newtype.shared.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 상담 이력 엔티티
 */
@Getter
@Table("counseling_history")
public class CounselingHistory extends BaseEntity {
    
    @Id
    @Column("id")
    private Long id;
    
    @Column("counseling_id")
    private Long counselingId;
    
    @Column("action_type")
    private String actionType;  // CREATED, ASSIGNED, STATUS_CHANGED, TRANSFERRED, ESCALATED, COMMENTED, RESOLVED, CLOSED
    
    // 액션 정보
    @Column("from_status")
    private String fromStatus;
    
    @Column("to_status")
    private String toStatus;
    
    @Column("from_user_id")
    private Long fromUserId;
    
    @Column("to_user_id")
    private Long toUserId;
    
    // 상세 정보
    @Column("comment")
    private String comment;
    
    @Column("reason")
    private String reason;  // 이관/에스컬레이션 사유
    
    // 메타데이터
    @Column("performed_by")
    private Long performedBy;  // 액션을 수행한 사용자
    
    @Column("performed_at")
    private LocalDateTime performedAt;
    
    /**
     * 기본 생성자 (Spring Data JDBC가 객체를 생성할 때 필요)
     */
    CounselingHistory() {
        // Spring Data JDBC가 조회 시 객체를 생성하기 위해 필요
    }
    
    @Builder
    public CounselingHistory(Long counselingId, String actionType, String fromStatus,
                            String toStatus, Long fromUserId, Long toUserId, String comment,
                            String reason, Long performedBy, LocalDateTime performedAt) {
        this.counselingId = counselingId;
        this.actionType = actionType;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.comment = comment;
        this.reason = reason;
        this.performedBy = performedBy;
        this.performedAt = performedAt != null ? performedAt : LocalDateTime.now();
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
    void setCounselingId(Long counselingId) {
        this.counselingId = counselingId;
    }
    
    void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }
    
    void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }
    
    void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }
    
    void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
    
    void setComment(String comment) {
        this.comment = comment;
    }
    
    void setReason(String reason) {
        this.reason = reason;
    }
    
    void setPerformedBy(Long performedBy) {
        this.performedBy = performedBy;
    }
    
    void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }
    
    /**
     * 액션 타입 Enum 조회
     * 주의: Spring Modulith 규칙상 internal에서 api를 직접 참조할 수 없으므로,
     * 이 메서드는 서비스 구현체에서만 사용됩니다.
     */
    public com.wrms.newtype.counseling.api.domain.ActionType getActionTypeEnum() {
        return actionType != null ? com.wrms.newtype.counseling.api.domain.ActionType.valueOf(actionType) : null;
    }
}

