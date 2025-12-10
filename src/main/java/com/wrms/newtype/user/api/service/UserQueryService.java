package com.wrms.newtype.user.api.service;

import com.wrms.newtype.shared.domain.Role;
import com.wrms.newtype.user.api.dto.response.UserResponse;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 Query 서비스 인터페이스
 */
public interface UserQueryService {
    
    /**
     * 사용자 ID로 조회
     */
    Optional<UserResponse> findById(Long id);
    
    /**
     * 사용자명으로 조회
     */
    Optional<UserResponse> findByUsername(String username);
    
    /**
     * 이메일로 조회
     */
    Optional<UserResponse> findByEmail(String email);
    
    /**
     * 역할별 사용자 조회
     */
    List<UserResponse> findByRole(Role role);
    
    /**
     * 부서별 사용자 조회
     */
    List<UserResponse> findByDepartment(String department);
    
    /**
     * 활성 사용자 조회
     */
    List<UserResponse> findActiveUsers();
    
    /**
     * 가용한 상담사 조회 (업무량 기준)
     */
    List<UserResponse> findAvailableCounselors();
}

