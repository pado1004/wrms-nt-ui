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

}
