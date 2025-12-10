package com.wrms.newtype.counseling.api.domain;

/**
 * 상담 상태
 */
public enum CounselingStatus {
    REGISTERED("등록됨"),
    ASSIGNED("할당됨"),
    IN_PROGRESS("처리중"),
    PENDING_INFO("고객 정보 대기중"),
    TRANSFERRED("이관됨"),
    ESCALATED("에스컬레이션됨"),
    RESOLVED("해결됨"),
    CLOSED("종료됨"),
    CANCELLED("취소됨");

    private final String description;

    CounselingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

