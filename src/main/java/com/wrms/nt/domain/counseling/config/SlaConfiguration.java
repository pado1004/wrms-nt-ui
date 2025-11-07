package com.wrms.nt.domain.counseling.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * SLA 모니터링 설정
 *
 * application.properties에서 sla.monitoring 속성들을 읽어옵니다.
 */
@Configuration
@ConfigurationProperties(prefix = "sla.monitoring")
public class SlaConfiguration {

    /**
     * SLA 경고 임계값 (분)
     * SLA 기한이 이 시간(분) 이내로 임박하면 경고 알림 발송
     */
    private int warningThresholdMinutes = 60;

    /**
     * SLA 모니터링 스케줄러 실행 주기 (cron)
     * 기본값: 5분마다 실행
     */
    private String scheduleCron = "0 */5 * * * *";

    /**
     * SLA 위반 시 자동 에스컬레이션 여부
     */
    private boolean autoEscalateOnViolation = false;

    /**
     * 스케줄러 활성화 여부
     */
    private boolean enabled = true;

    // Getters and Setters

    public int getWarningThresholdMinutes() {
        return warningThresholdMinutes;
    }

    public void setWarningThresholdMinutes(int warningThresholdMinutes) {
        this.warningThresholdMinutes = warningThresholdMinutes;
    }

    public String getScheduleCron() {
        return scheduleCron;
    }

    public void setScheduleCron(String scheduleCron) {
        this.scheduleCron = scheduleCron;
    }

    public boolean isAutoEscalateOnViolation() {
        return autoEscalateOnViolation;
    }

    public void setAutoEscalateOnViolation(boolean autoEscalateOnViolation) {
        this.autoEscalateOnViolation = autoEscalateOnViolation;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
