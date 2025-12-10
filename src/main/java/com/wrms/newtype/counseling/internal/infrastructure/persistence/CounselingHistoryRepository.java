package com.wrms.newtype.counseling.internal.infrastructure.persistence;

import com.wrms.newtype.counseling.internal.domain.CounselingHistory;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 상담 이력 리포지토리
 */
public interface CounselingHistoryRepository extends CrudRepository<CounselingHistory, Long> {
    
    /**
     * 상담 ID로 이력 목록 조회 (최신순)
     */
    @Query("SELECT * FROM counseling_history WHERE counseling_id = :counselingId ORDER BY performed_at DESC")
    List<CounselingHistory> findByCounselingIdOrderByPerformedAtDesc(@Param("counselingId") Long counselingId);
    
    /**
     * 액션 타입별 이력 조회
     */
    @Query("SELECT * FROM counseling_history WHERE action_type = :actionType ORDER BY performed_at DESC")
    List<CounselingHistory> findByActionType(@Param("actionType") String actionType);
}

