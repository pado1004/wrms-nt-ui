package com.wrms.newtype.counseling.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 상담 코멘트 추가 요청 DTO
 */
public record AddCommentRequest(
    @NotBlank(message = "코멘트는 필수입니다.")
    String comment,
    
    @NotNull(message = "수행자 ID는 필수입니다.")
    Long performedBy
) {}

