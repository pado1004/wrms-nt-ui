package com.wrms.newtype.counseling.api.service;

import com.wrms.newtype.counseling.api.dto.response.CounselingResponse;

import java.util.List;

/**
 * 상담 Query 서비스 인터페이스
 * 상담 조회 작업을 담당합니다.
 */
public interface CounselingQueryService {
    
    /**
     * 상담 ID로 조회
     * @param id 상담 ID
     * @return 상담 정보
     */
    CounselingResponse findById(Long id);
    
    /**
     * 모든 상담 목록 조회
     * @return 상담 목록
     */
    List<CounselingResponse> findAll();
    
    /**
     * 상태별 상담 목록 조회
     * @param status 상태
     * @return 상담 목록
     */
    List<CounselingResponse> findByStatus(String status);
    
    /**
     * 상담사별 상담 목록 조회
     * @param counselorId 상담사 ID
     * @return 상담 목록
     */
    List<CounselingResponse> findByCounselorId(Long counselorId);
    
    /**
     * 고객명으로 상담 검색
     * @param customerName 고객명
     * @return 상담 목록
     */
    List<CounselingResponse> searchByCustomerName(String customerName);
    
    /**
     * SLA 위반 상담 조회
     * @return SLA 위반 상담 목록
     */
    List<CounselingResponse> findSlaViolatedCounselings();
    
    /**
     * 상담 이력 조회
     * @param counselingId 상담 ID
     * @return 상담 이력 목록
     */
    List<com.wrms.newtype.counseling.api.dto.response.CounselingHistoryResponse> findHistoryByCounselingId(Long counselingId);
}

