package com.wrms.newtype.counseling.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 상담 해결 요청 DTO
 */
public record ResolveCounselingRequest(
    @NotBlank(message = "해결 내용은 필수입니다.")
    String resolution,
    
    @NotNull(message = "수행자 ID는 필수입니다.")
    Long performedBy
) {}

