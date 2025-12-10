package com.wrms.newtype.shared.domain;

/**
 * 상담 우선순위
 */
public enum Priority {
    URGENT("긴급", 4),      // 4시간 이내 처리
    HIGH("높음", 24),       // 24시간 이내 처리
    NORMAL("보통", 72),     // 72시간(3일) 이내 처리
    LOW("낮음", 168);       // 168시간(7일) 이내 처리

    private final String description;
    private final int slaHours;  // SLA 시간 (시간 단위)

    Priority(String description, int slaHours) {
        this.description = description;
        this.slaHours = slaHours;
    }

    public String getDescription() {
        return description;
    }

    public int getSlaHours() {
        return slaHours;
    }
}

