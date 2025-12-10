package com.wrms.newtype.counseling.api.dto.response;

import com.wrms.newtype.counseling.api.domain.ActionType;

import java.time.LocalDateTime;

/**
 * 상담 이력 응답 DTO
 */
public record CounselingHistoryResponse(
    Long id,
    Long counselingId,
    ActionType actionType,
    String fromStatus,
    String toStatus,
    Long fromUserId,
    Long toUserId,
    String comment,
    String reason,
    Long performedBy,
    LocalDateTime performedAt
) {
    /**
     * 도메인 엔티티로부터 Response DTO를 생성합니다.
     * 
     * 주의: Spring Modulith 규칙상 api 패키지에서 internal 패키지를 직접 참조할 수 없으므로,
     * 이 메서드는 서비스 구현체(internal.application)에서만 사용됩니다.
     * 
     * @param id 이력 ID
     * @param counselingId 상담 ID
     * @param actionType 액션 타입
     * @param fromStatus 이전 상태
     * @param toStatus 이후 상태
     * @param fromUserId 이전 사용자 ID
     * @param toUserId 이후 사용자 ID
     * @param comment 코멘트
     * @param reason 사유
     * @param performedBy 수행자 ID
     * @param performedAt 수행 시간
     * @return Response DTO
     */
    public static CounselingHistoryResponse of(Long id, Long counselingId, ActionType actionType,
                                               String fromStatus, String toStatus, Long fromUserId, Long toUserId,
                                               String comment, String reason, Long performedBy,
                                               java.time.LocalDateTime performedAt) {
        return new CounselingHistoryResponse(
            id,
            counselingId,
            actionType,
            fromStatus,
            toStatus,
            fromUserId,
            toUserId,
            comment,
            reason,
            performedBy,
            performedAt
        );
    }
}

