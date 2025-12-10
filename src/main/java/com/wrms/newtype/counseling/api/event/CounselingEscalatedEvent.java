package com.wrms.newtype.counseling.api.event;

/**
 * 상담 에스컬레이션 이벤트
 */
public record CounselingEscalatedEvent(
    Long counselingId,
    Integer escalationLevel,
    Long escalatedBy,
    String reason
) {}

