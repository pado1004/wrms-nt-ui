package com.wrms.newtype.notification.api.service;

import com.wrms.newtype.notification.api.dto.response.NotificationResponse;

import java.util.List;

/**
 * 알림 Query 서비스 인터페이스
 */
public interface NotificationQueryService {
    
    /**
     * 사용자의 모든 알림 조회
     */
    List<NotificationResponse> findByUserId(Long userId);
    
    /**
     * 사용자의 읽지 않은 알림 조회
     */
    List<NotificationResponse> findUnreadByUserId(Long userId);
    
    /**
     * 사용자의 읽지 않은 알림 개수
     */
    int countUnreadByUserId(Long userId);
    
    /**
     * 사용자의 최근 N개 알림 조회
     */
    List<NotificationResponse> findRecentByUserId(Long userId, int limit);
}

