package com.wrms.newtype.customer.api.service;

import com.wrms.newtype.customer.api.dto.response.CustomerResponse;

import java.util.List;

/**
 * 고객 Query 서비스 인터페이스
 */
public interface CustomerQueryService {
    
    /**
     * 고객 ID로 조회
     */
    CustomerResponse findById(Long id);
    
    /**
     * 모든 고객 목록 조회
     */
    List<CustomerResponse> findAll();
    
    /**
     * 이름으로 검색
     */
    List<CustomerResponse> searchByName(String name);
}

