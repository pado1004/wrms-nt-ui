package com.wrms.nt.domain.counseling.service;

import com.wrms.nt.domain.counseling.config.SlaConfiguration;
import com.wrms.nt.domain.counseling.entity.Counseling;
import com.wrms.nt.domain.counseling.repository.CounselingRepository;
import com.wrms.nt.domain.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SLA 모니터링 서비스
 *
 * 주기적으로 상담의 SLA 상태를 체크하고 필요한 조치를 취합니다.
 * - SLA 임박 상담 탐지 및 경고 알림 발송
 * - SLA 위반 상담 탐지 및 알림 발송
 * - 옵션: SLA 위반 시 자동 에스컬레이션
 */
@Service
public class SlaMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(SlaMonitoringService.class);

    private final CounselingRepository counselingRepository;
    private final NotificationService notificationService;
    private final SlaConfiguration slaConfiguration;

    // 이미 경고 알림을 보낸 상담 ID 추적 (중복 알림 방지)
    private final Set<Long> warnedCounselingIds = new HashSet<>();

    // 이미 위반 알림을 보낸 상담 ID 추적 (중복 알림 방지)
    private final Set<Long> violatedCounselingIds = new HashSet<>();

    public SlaMonitoringService(
            CounselingRepository counselingRepository,
            NotificationService notificationService,
            SlaConfiguration slaConfiguration) {
        this.counselingRepository = counselingRepository;
        this.notificationService = notificationService;
        this.slaConfiguration = slaConfiguration;
    }

    /**
     * SLA 모니터링 스케줄러
     *
     * 기본값: 5분마다 실행
     * application.properties에서 sla.monitoring.schedule-cron으로 조정 가능
     */
    @Scheduled(cron = "${sla.monitoring.schedule-cron:0 */5 * * * *}")
    @Transactional
    public void monitorSla() {
        if (!slaConfiguration.isEnabled()) {
            logger.debug("SLA monitoring is disabled");
            return;
        }

        logger.info("Starting SLA monitoring...");

        try {
            // 1. SLA 임박 상담 체크
            checkApproachingSlaCounselings();

            // 2. SLA 위반 상담 체크
            checkViolatedSlaCounselings();

            logger.info("SLA monitoring completed successfully");
        } catch (Exception e) {
            logger.error("Error during SLA monitoring", e);
        }
    }

    /**
     * SLA 임박 상담 체크 및 경고 알림 발송
     */
    private void checkApproachingSlaCounselings() {
        int warningThreshold = slaConfiguration.getWarningThresholdMinutes();
        List<Counseling> approachingCounselings =
            counselingRepository.findSlaApproachingCounselings(warningThreshold);

        logger.info("Found {} counseling(s) approaching SLA deadline", approachingCounselings.size());

        for (Counseling counseling : approachingCounselings) {
            // 이미 경고 알림을 보낸 경우 스킵
            if (warnedCounselingIds.contains(counseling.getId())) {
                continue;
            }

            // 담당자가 있는 경우에만 알림 발송
            if (counseling.getCounselorId() != null) {
                long minutesRemaining = ChronoUnit.MINUTES.between(
                    LocalDateTime.now(),
                    counseling.getSlaDueDate()
                );

                notificationService.createSlaWarningNotification(
                    counseling.getCounselorId(),
                    counseling.getId(),
                    counseling.getCustomerName(),
                    counseling.getSlaDueDate(),
                    minutesRemaining
                );

                // 경고 알림 발송 기록
                warnedCounselingIds.add(counseling.getId());

                logger.info("SLA warning notification sent for counseling ID: {} ({}minutes remaining)",
                    counseling.getId(), minutesRemaining);
            }
        }
    }

    /**
     * SLA 위반 상담 체크 및 알림 발송
     */
    private void checkViolatedSlaCounselings() {
        List<Counseling> violatedCounselings = counselingRepository.findSlaViolatedCounselings();

        logger.info("Found {} counseling(s) with SLA violation", violatedCounselings.size());

        for (Counseling counseling : violatedCounselings) {
            // 이미 위반 알림을 보낸 경우 스킵
            if (violatedCounselingIds.contains(counseling.getId())) {
                continue;
            }

            // 담당자가 있는 경우에만 알림 발송
            if (counseling.getCounselorId() != null) {
                long hoursViolated = ChronoUnit.HOURS.between(
                    counseling.getSlaDueDate(),
                    LocalDateTime.now()
                );

                notificationService.createSlaViolatedNotification(
                    counseling.getCounselorId(),
                    counseling.getId(),
                    counseling.getCustomerName(),
                    counseling.getSlaDueDate(),
                    hoursViolated
                );

                // 위반 알림 발송 기록
                violatedCounselingIds.add(counseling.getId());

                logger.warn("SLA violated for counseling ID: {} ({} hours overdue)",
                    counseling.getId(), hoursViolated);

                // 자동 에스컬레이션이 활성화되어 있고, 아직 에스컬레이션되지 않은 경우
                if (slaConfiguration.isAutoEscalateOnViolation() && counseling.getEscalationLevel() == 0) {
                    // TODO: 자동 에스컬레이션 로직 추가
                    logger.info("Auto-escalation would trigger for counseling ID: {} (feature not yet implemented)",
                        counseling.getId());
                }
            }
        }
    }

    /**
     * 상담이 해결되거나 종료되면 추적 목록에서 제거
     * (추후 이벤트 리스너로 연동 가능)
     */
    public void clearTracking(Long counselingId) {
        warnedCounselingIds.remove(counselingId);
        violatedCounselingIds.remove(counselingId);
    }

    /**
     * 수동 SLA 체크 트리거 (관리자 기능)
     */
    public void triggerManualCheck() {
        logger.info("Manual SLA check triggered");
        monitorSla();
    }

    /**
     * SLA 통계 조회
     */
    public SlaStatistics getStatistics() {
        List<Counseling> approaching = counselingRepository.findSlaApproachingCounselings(
            slaConfiguration.getWarningThresholdMinutes()
        );
        List<Counseling> violated = counselingRepository.findSlaViolatedCounselings();

        return new SlaStatistics(
            approaching.size(),
            violated.size(),
            warnedCounselingIds.size(),
            violatedCounselingIds.size()
        );
    }

    /**
     * SLA 통계 데이터 클래스
     */
    public static class SlaStatistics {
        private final int approachingCount;
        private final int violatedCount;
        private final int warnedCount;
        private final int violationNotifiedCount;

        public SlaStatistics(int approachingCount, int violatedCount,
                           int warnedCount, int violationNotifiedCount) {
            this.approachingCount = approachingCount;
            this.violatedCount = violatedCount;
            this.warnedCount = warnedCount;
            this.violationNotifiedCount = violationNotifiedCount;
        }

        public int getApproachingCount() {
            return approachingCount;
        }

        public int getViolatedCount() {
            return violatedCount;
        }

        public int getWarnedCount() {
            return warnedCount;
        }

        public int getViolationNotifiedCount() {
            return violationNotifiedCount;
        }
    }
}
