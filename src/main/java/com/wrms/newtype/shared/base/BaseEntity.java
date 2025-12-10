package com.wrms.newtype.shared.base;

import java.time.LocalDateTime;

/**
 * 모든 도메인 엔티티의 기본 클래스
 * 생성 시간과 수정 시간을 자동으로 관리합니다.
 */
public abstract class BaseEntity {
    
    /**
     * 생성 시간
     */
    private LocalDateTime createdAt;
    
    /**
     * 수정 시간
     */
    private LocalDateTime updatedAt;
    
    /**
     * 타임스탬프 초기화
     * 엔티티 생성 시 호출되어야 합니다.
     */
    protected void initializeTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    /**
     * 타임스탬프 업데이트
     * 엔티티 수정 시 호출되어야 합니다.
     */
    protected void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 생성 시간 조회
     * @return 생성 시간
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * 수정 시간 조회
     * @return 수정 시간
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * 생성 시간 설정 (Spring Data JDBC용)
     * @param createdAt 생성 시간
     */
    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * 수정 시간 설정 (Spring Data JDBC용)
     * @param updatedAt 수정 시간
     */
    void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

