package com.wrms.newtype.counseling.api.event;

/**
 * 상담 해결 이벤트
 */
public record CounselingResolvedEvent(
    Long counselingId,
    Long resolvedBy
) {}

