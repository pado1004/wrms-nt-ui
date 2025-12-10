package com.wrms.newtype.counseling.api.dto.request;

import com.wrms.newtype.shared.domain.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 상담 생성 요청 DTO
 */
public record CreateCounselingRequest(
    @NotBlank(message = "고객명은 필수입니다.")
    String customerName,
    
    String contact,
    
    String counselingType,
    
    @NotBlank(message = "상담 내용은 필수입니다.")
    String content,
    
    @NotNull(message = "우선순위는 필수입니다.")
    Priority priority,
    
    Long createdBy
) {}

