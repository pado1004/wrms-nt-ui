package com.wrms.newtype.counseling.api.service;

import com.wrms.newtype.counseling.api.dto.request.*;
import com.wrms.newtype.counseling.api.dto.response.CounselingResponse;

/**
 * 상담 Command 서비스 인터페이스
 * 상담 생성, 수정, 삭제 등의 명령 작업을 담당합니다.
 */
public interface CounselingCommandService {
    
    /**
     * 상담 생성
     * @param request 생성 요청
     * @return 생성된 상담 정보
     */
    CounselingResponse create(CreateCounselingRequest request);
    
    /**
     * 상담 상태 변경
     * @param id 상담 ID
     * @param request 상태 변경 요청
     */
    void updateStatus(Long id, UpdateCounselingStatusRequest request);
    
    /**
     * 상담 이관
     * @param id 상담 ID
     * @param request 이관 요청
     * @return 이관된 상담 정보
     */
    CounselingResponse transfer(Long id, TransferCounselingRequest request);
    
    /**
     * 상담 에스컬레이션
     * @param id 상담 ID
     * @param request 에스컬레이션 요청
     * @return 에스컬레이션된 상담 정보
     */
    CounselingResponse escalate(Long id, EscalateCounselingRequest request);
    
    /**
     * 상담 해결
     * @param id 상담 ID
     * @param request 해결 요청
     * @return 해결된 상담 정보
     */
    CounselingResponse resolve(Long id, ResolveCounselingRequest request);
    
    /**
     * 상담 종료
     * @param id 상담 ID
     * @param performedBy 수행자 ID
     * @return 종료된 상담 정보
     */
    CounselingResponse close(Long id, Long performedBy);
    
    /**
     * 코멘트 추가
     * @param id 상담 ID
     * @param request 코멘트 추가 요청
     */
    void addComment(Long id, AddCommentRequest request);
    
    /**
     * 상담 삭제
     * @param id 상담 ID
     */
    void delete(Long id);
}

