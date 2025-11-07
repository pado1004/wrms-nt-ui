package com.wrms.nt.domain.notification.service;

import com.wrms.nt.domain.notification.entity.Notification;
import com.wrms.nt.domain.notification.entity.NotificationType;
import com.wrms.nt.domain.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 서비스
 */
@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * 알림 생성
     */
    public Notification createNotification(Long userId, Long counselingId, NotificationType type,
                                          String title, String message) {
        Notification notification = new Notification(userId, counselingId, type.name(), title, message);
        return notificationRepository.save(notification);
    }

    /**
     * 사용자의 모든 알림 조회
     */
    @Transactional(readOnly = true)
    public List<Notification> findByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 사용자의 읽지 않은 알림 조회
     */
    @Transactional(readOnly = true)
    public List<Notification> findUnreadByUserId(Long userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }

    /**
     * 사용자의 읽지 않은 알림 개수
     */
    @Transactional(readOnly = true)
    public int countUnreadByUserId(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * 사용자의 최근 N개 알림 조회
     */
    @Transactional(readOnly = true)
    public List<Notification> findRecentByUserId(Long userId, int limit) {
        return notificationRepository.findRecentByUserId(userId, limit);
    }

    /**
     * 알림 읽음 처리
     */
    public void markAsRead(Long notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    /**
     * 사용자의 모든 알림 읽음 처리
     */
    public void markAllAsReadByUserId(Long userId) {
        List<Notification> unreadNotifications = findUnreadByUserId(userId);
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    /**
     * 알림 삭제
     */
    public void delete(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    /**
     * 상담 할당 알림 생성
     */
    public Notification createAssignedNotification(Long userId, Long counselingId, String customerName) {
        String title = "새 상담이 할당되었습니다";
        String message = String.format("'%s' 고객의 상담이 할당되었습니다. (상담 #%d)", customerName, counselingId);
        return createNotification(userId, counselingId, NotificationType.ASSIGNED, title, message);
    }

    /**
     * 상담 이관 알림 생성
     */
    public Notification createTransferredNotification(Long userId, Long counselingId, String customerName, String fromUser) {
        String title = "상담이 이관되었습니다";
        String message = String.format("'%s'님으로부터 '%s' 고객의 상담이 이관되었습니다. (상담 #%d)",
            fromUser, customerName, counselingId);
        return createNotification(userId, counselingId, NotificationType.TRANSFERRED, title, message);
    }

    /**
     * 에스컬레이션 알림 생성
     */
    public Notification createEscalatedNotification(Long userId, Long counselingId, String customerName, int level) {
        String title = "상담이 에스컬레이션되었습니다";
        String message = String.format("'%s' 고객의 상담이 레벨 %d로 에스컬레이션되었습니다. (상담 #%d)",
            customerName, level, counselingId);
        return createNotification(userId, counselingId, NotificationType.ESCALATED, title, message);
    }

    /**
     * SLA 경고 알림 생성
     */
    public Notification createSlaWarningNotification(Long userId, Long counselingId, String customerName) {
        String title = "SLA 기한이 임박했습니다";
        String message = String.format("'%s' 고객의 상담 SLA 기한이 임박했습니다. (상담 #%d)", customerName, counselingId);
        return createNotification(userId, counselingId, NotificationType.SLA_WARNING, title, message);
    }

    /**
     * SLA 위반 알림 생성
     */
    public Notification createSlaViolatedNotification(Long userId, Long counselingId, String customerName) {
        String title = "SLA 기한이 초과되었습니다";
        String message = String.format("'%s' 고객의 상담 SLA 기한이 초과되었습니다. (상담 #%d)", customerName, counselingId);
        return createNotification(userId, counselingId, NotificationType.SLA_VIOLATED, title, message);
    }

    /**
     * 코멘트 추가 알림 생성
     */
    public Notification createCommentAddedNotification(Long userId, Long counselingId, String customerName, String commenter) {
        String title = "새 코멘트가 추가되었습니다";
        String message = String.format("'%s'님이 '%s' 고객 상담에 코멘트를 추가했습니다. (상담 #%d)",
            commenter, customerName, counselingId);
        return createNotification(userId, counselingId, NotificationType.COMMENT_ADDED, title, message);
    }
}
