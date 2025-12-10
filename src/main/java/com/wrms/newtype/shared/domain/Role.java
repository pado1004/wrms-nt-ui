package com.wrms.newtype.shared.domain;

/**
 * 사용자 역할
 */
public enum Role {
    COUNSELOR("일반 상담사"),
    SENIOR_COUNSELOR("시니어 상담사"),
    MANAGER("팀 관리자"),
    ADMIN("시스템 관리자");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

