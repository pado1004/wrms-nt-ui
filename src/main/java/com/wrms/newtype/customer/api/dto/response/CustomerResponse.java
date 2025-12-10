package com.wrms.newtype.customer.api.dto.response;

import java.time.LocalDateTime;

/**
 * 고객 응답 DTO
 */
public record CustomerResponse(
    Long id,
    String name,
    String email,
    String phone,
    String address,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    /**
     * 도메인 엔티티로부터 Response DTO를 생성합니다.
     */
    public static CustomerResponse from(com.wrms.newtype.customer.internal.domain.Customer entity) {
        return new CustomerResponse(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getAddress(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}

