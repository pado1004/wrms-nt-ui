package com.wrms.newtype.customer.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 고객 수정 요청 DTO
 */
public record UpdateCustomerRequest(
    @NotBlank(message = "이름은 필수입니다.")
    String name,
    
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,
    
    String phone,
    
    String address
) {}

