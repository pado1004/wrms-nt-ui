package com.wrms.newtype.user.api.dto.response;

import com.wrms.newtype.shared.domain.Role;

import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO
 */
public record UserResponse(
    Long id,
    String username,
    String name,
    String email,
    String phone,
    Role role,
    String department,
    Boolean isActive,
    Integer maxConcurrentCases,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    /**
     * 도메인 엔티티로부터 Response DTO를 생성합니다.
     */
    public static UserResponse from(com.wrms.newtype.user.internal.domain.User entity) {
        return new UserResponse(
            entity.getId(),
            entity.getUsername(),
            entity.getName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getRoleEnum(),
            entity.getDepartment(),
            entity.getIsActive(),
            entity.getMaxConcurrentCases(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}

