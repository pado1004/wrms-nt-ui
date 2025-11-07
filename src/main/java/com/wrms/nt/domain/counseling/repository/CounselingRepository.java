package com.wrms.nt.domain.counseling.repository;

import com.wrms.nt.domain.counseling.entity.Counseling;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 상담 리포지토리
 */
@Repository
public interface CounselingRepository extends CrudRepository<Counseling, Long> {

    /**
     * 모든 상담 목록 조회 (최신순)
     */
    @Query("SELECT * FROM counseling ORDER BY created_at DESC")
    List<Counseling> findAllOrderByCreatedAtDesc();

    /**
     * 상태별 상담 목록 조회
     */
    @Query("SELECT * FROM counseling WHERE status = :status ORDER BY created_at DESC")
    List<Counseling> findByStatus(@Param("status") String status);

    /**
     * 상담사별 상담 목록 조회
     */
    @Query("SELECT * FROM counseling WHERE counselor_id = :counselorId ORDER BY created_at DESC")
    List<Counseling> findByCounselorId(@Param("counselorId") Long counselorId);

    /**
     * 고객명으로 상담 검색
     */
    @Query("SELECT * FROM counseling WHERE customer_name LIKE :customerName ORDER BY created_at DESC")
    List<Counseling> findByCustomerNameContaining(@Param("customerName") String customerName);

    /**
     * SLA 임박 상담 조회
     * 현재 시간으로부터 지정된 시간(분) 이내에 SLA 기한이 도래하는 미해결 상담 조회
     */
    @Query("SELECT * FROM counseling " +
           "WHERE sla_due_date IS NOT NULL " +
           "AND sla_due_date > CURRENT_TIMESTAMP " +
           "AND sla_due_date <= DATEADD('MINUTE', :withinMinutes, CURRENT_TIMESTAMP) " +
           "AND status NOT IN ('RESOLVED', 'CLOSED', 'CANCELLED') " +
           "ORDER BY sla_due_date ASC")
    List<Counseling> findSlaApproachingCounselings(@Param("withinMinutes") int withinMinutes);

    /**
     * SLA 위반 상담 조회
     * SLA 기한이 초과된 미해결 상담 조회
     */
    @Query("SELECT * FROM counseling " +
           "WHERE sla_due_date IS NOT NULL " +
           "AND sla_due_date < CURRENT_TIMESTAMP " +
           "AND status NOT IN ('RESOLVED', 'CLOSED', 'CANCELLED') " +
           "ORDER BY sla_due_date ASC")
    List<Counseling> findSlaViolatedCounselings();

    /**
     * 특정 우선순위의 SLA 위반 상담 개수 조회
     */
    @Query("SELECT COUNT(*) FROM counseling " +
           "WHERE sla_due_date IS NOT NULL " +
           "AND sla_due_date < CURRENT_TIMESTAMP " +
           "AND priority = :priority " +
           "AND status NOT IN ('RESOLVED', 'CLOSED', 'CANCELLED')")
    int countSlaViolatedByPriority(@Param("priority") String priority);

}
