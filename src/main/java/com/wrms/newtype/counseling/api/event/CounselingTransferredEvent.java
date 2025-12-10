package com.wrms.newtype.counseling.api.event;

/**
 * 상담 이관 이벤트
 */
public record CounselingTransferredEvent(
    Long counselingId,
    Long fromCounselorId,
    Long toCounselorId,
    String reason
) {}

