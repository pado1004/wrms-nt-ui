package com.wrms.newtype.counseling.api.dto.response;

import com.wrms.newtype.counseling.api.domain.CounselingStatus;
import com.wrms.newtype.shared.domain.Priority;

import java.time.LocalDateTime;

/**
 * 상담 응답 DTO
 */
public record CounselingResponse(
    Long id,
    String customerName,
    String contact,
    String counselingType,
    String content,
    CounselingStatus status,
    Priority priority,
    Long counselorId,
    Long assignedBy,
    Long originalCounselorId,
    Integer escalationLevel,
    LocalDateTime escalatedAt,
    Long escalatedBy,
    String escalationReason,
    Integer transferCount,
    LocalDateTime lastTransferredAt,
    LocalDateTime slaDueDate,
    LocalDateTime firstResponseAt,
    LocalDateTime resolvedAt,
    String resolution,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    /**
     * 도메인 엔티티로부터 Response DTO를 생성합니다.
     * 
     * 주의: Spring Modulith 규칙상 api 패키지에서 internal 패키지를 직접 참조할 수 없으므로,
     * 이 메서드는 서비스 구현체(internal.application)에서만 사용됩니다.
     * 
     * @param entity 도메인 엔티티
     * @return Response DTO
     */
    public static CounselingResponse from(com.wrms.newtype.counseling.internal.domain.Counseling entity) {
        return new CounselingResponse(
            entity.getId(),
            entity.getCustomerName(),
            entity.getContact(),
            entity.getCounselingType(),
            entity.getContent(),
            entity.getStatusEnum(),
            entity.getPriorityEnum(),
            entity.getCounselorId(),
            entity.getAssignedBy(),
            entity.getOriginalCounselorId(),
            entity.getEscalationLevel(),
            entity.getEscalatedAt(),
            entity.getEscalatedBy(),
            entity.getEscalationReason(),
            entity.getTransferCount(),
            entity.getLastTransferredAt(),
            entity.getSlaDueDate(),
            entity.getFirstResponseAt(),
            entity.getResolvedAt(),
            entity.getResolution(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}

