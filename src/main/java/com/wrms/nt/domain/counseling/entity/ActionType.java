package com.wrms.nt.domain.counseling.entity;

/**
 * 상담 이력 액션 타입
 */
public enum ActionType {
    CREATED("생성됨"),
    ASSIGNED("할당됨"),
    STATUS_CHANGED("상태 변경"),
    TRANSFERRED("이관됨"),
    ESCALATED("에스컬레이션됨"),
    COMMENTED("메모 추가"),
    RESOLVED("해결됨"),
    CLOSED("종료됨");

    private final String description;

    ActionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
