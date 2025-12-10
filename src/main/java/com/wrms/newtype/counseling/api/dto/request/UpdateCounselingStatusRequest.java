package com.wrms.newtype.counseling.api.dto.request;

import com.wrms.newtype.counseling.api.domain.CounselingStatus;
import jakarta.validation.constraints.NotNull;

/**
 * 상담 상태 변경 요청 DTO
 */
public record UpdateCounselingStatusRequest(
    @NotNull(message = "상태는 필수입니다.")
    CounselingStatus status,
    
    @NotNull(message = "수행자 ID는 필수입니다.")
    Long performedBy
) {}

