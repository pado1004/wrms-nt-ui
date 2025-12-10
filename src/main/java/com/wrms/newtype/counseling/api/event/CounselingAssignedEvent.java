package com.wrms.newtype.counseling.api.event;

/**
 * 상담 할당 이벤트
 */
public record CounselingAssignedEvent(
    Long counselingId,
    Long counselorId,
    Long assignedBy
) {}

