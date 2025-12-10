package com.wrms.newtype.counseling.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 상담 에스컬레이션 요청 DTO
 */
public record EscalateCounselingRequest(
    @NotBlank(message = "에스컬레이션 사유는 필수입니다.")
    String reason,
    
    String comment,
    
    @NotNull(message = "수행자 ID는 필수입니다.")
    Long performedBy
) {}

