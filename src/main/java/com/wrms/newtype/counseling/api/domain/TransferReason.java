package com.wrms.newtype.counseling.api.domain;

/**
 * 이관 사유
 */
public enum TransferReason {
    WORKLOAD_DISTRIBUTION("업무 분산"),
    SPECIALIST_REQUIRED("전문가 필요"),
    UNAVAILABLE("담당자 부재"),
    CUSTOMER_REQUEST("고객 요청"),
    OTHER("기타");

    private final String description;

    TransferReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

