package com.wrms.newtype.counseling.api.dto.request;

import com.wrms.newtype.counseling.api.domain.TransferReason;
import jakarta.validation.constraints.NotNull;

/**
 * 상담 이관 요청 DTO
 */
public record TransferCounselingRequest(
    @NotNull(message = "이관 대상 상담사 ID는 필수입니다.")
    Long toCounselorId,
    
    @NotNull(message = "이관 사유는 필수입니다.")
    TransferReason reason,
    
    String comment,
    
    @NotNull(message = "수행자 ID는 필수입니다.")
    Long performedBy
) {}

