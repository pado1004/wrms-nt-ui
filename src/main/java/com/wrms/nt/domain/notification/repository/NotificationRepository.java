package com.wrms.nt.domain.notification.repository;

import com.wrms.nt.domain.notification.entity.Notification;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 알림 리포지토리
 */
@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {

    /**
     * 사용자의 모든 알림 조회 (최신순)
     */
    @Query("SELECT * FROM notification WHERE user_id = :userId ORDER BY created_at DESC")
    List<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * 사용자의 읽지 않은 알림 조회
     */
    @Query("SELECT * FROM notification WHERE user_id = :userId AND is_read = FALSE ORDER BY created_at DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    /**
     * 사용자의 읽지 않은 알림 개수
     */
    @Query("SELECT COUNT(*) FROM notification WHERE user_id = :userId AND is_read = FALSE")
    int countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 상담별 알림 조회
     */
    @Query("SELECT * FROM notification WHERE counseling_id = :counselingId ORDER BY created_at DESC")
    List<Notification> findByCounselingId(@Param("counselingId") Long counselingId);

    /**
     * 사용자의 최근 N개 알림 조회
     */
    @Query("SELECT * FROM notification WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    List<Notification> findRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);
}
