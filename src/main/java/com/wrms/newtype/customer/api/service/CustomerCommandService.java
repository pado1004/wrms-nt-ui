package com.wrms.newtype.customer.api.service;

import com.wrms.newtype.customer.api.dto.request.CreateCustomerRequest;
import com.wrms.newtype.customer.api.dto.request.UpdateCustomerRequest;
import com.wrms.newtype.customer.api.dto.response.CustomerResponse;

/**
 * 고객 Command 서비스 인터페이스
 */
public interface CustomerCommandService {
    
    /**
     * 고객 생성
     */
    CustomerResponse create(CreateCustomerRequest request);
    
    /**
     * 고객 수정
     */
    void update(Long id, UpdateCustomerRequest request);
    
    /**
     * 고객 삭제
     */
    void delete(Long id);
}

