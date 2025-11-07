package com.wrms.nt.domain.counseling.repository;

import com.wrms.nt.domain.counseling.entity.CounselingHistory;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 상담 이력 리포지토리
 */
@Repository
public interface CounselingHistoryRepository extends CrudRepository<CounselingHistory, Long> {

    /**
     * 상담 ID로 이력 조회 (최신순)
     */
    @Query("SELECT * FROM counseling_history WHERE counseling_id = :counselingId ORDER BY performed_at DESC")
    List<CounselingHistory> findByCounselingIdOrderByPerformedAtDesc(@Param("counselingId") Long counselingId);

    /**
     * 상담 ID와 액션 타입으로 조회
     */
    @Query("SELECT * FROM counseling_history WHERE counseling_id = :counselingId AND action_type = :actionType ORDER BY performed_at DESC")
    List<CounselingHistory> findByCounselingIdAndActionType(
        @Param("counselingId") Long counselingId,
        @Param("actionType") String actionType
    );

    /**
     * 사용자가 수행한 이력 조회
     */
    @Query("SELECT * FROM counseling_history WHERE performed_by = :userId ORDER BY performed_at DESC")
    List<CounselingHistory> findByPerformedBy(@Param("userId") Long userId);
}
