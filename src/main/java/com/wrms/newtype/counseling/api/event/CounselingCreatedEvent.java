package com.wrms.newtype.counseling.api.event;

/**
 * 상담 생성 이벤트
 * 다른 모듈에서 이 이벤트를 구독하여 후속 작업을 수행할 수 있습니다.
 */
public record CounselingCreatedEvent(
    Long counselingId,
    String customerName,
    Long counselorId
) {}

