package com.wrms.nt.domain.counseling.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 상담 이력 엔티티
 */
@Table("counseling_history")
public class CounselingHistory {

    @Id
    private Long id;

    private Long counselingId;
    private String actionType;  // CREATED, ASSIGNED, STATUS_CHANGED, TRANSFERRED, ESCALATED, COMMENTED, RESOLVED, CLOSED

    // 액션 정보
    private String fromStatus;
    private String toStatus;
    private Long fromUserId;
    private Long toUserId;

    // 상세 정보
    private String comment;
    private String reason;  // 이관/에스컬레이션 사유

    // 메타데이터
    private Long performedBy;  // 액션을 수행한 사용자
    private LocalDateTime performedAt;

    // Constructor
    public CounselingHistory() {
    }

    public CounselingHistory(Long counselingId, String actionType, Long performedBy) {
        this.counselingId = counselingId;
        this.actionType = actionType;
        this.performedBy = performedBy;
        this.performedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCounselingId() {
        return counselingId;
    }

    public void setCounselingId(Long counselingId) {
        this.counselingId = counselingId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public ActionType getActionTypeEnum() {
        return actionType != null ? ActionType.valueOf(actionType) : null;
    }

    public void setActionTypeEnum(ActionType actionType) {
        this.actionType = actionType != null ? actionType.name() : null;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(Long performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }
}
