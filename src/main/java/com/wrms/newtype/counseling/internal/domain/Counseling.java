package com.wrms.newtype.counseling.internal.domain;

import com.wrms.newtype.shared.base.BaseEntity;
import com.wrms.newtype.shared.domain.Priority;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 상담 엔티티
 */
@Getter
@Table("counseling")
public class Counseling extends BaseEntity {
    
    @Id
    @Column("id")
    private Long id;
    
    // 기본 정보
    @Column("customer_name")
    private String customerName;
    
    @Column("contact")
    private String contact;
    
    @Column("counseling_type")
    private String counselingType;
    
    @Column("content")
    private String content;
    
    // 상태 및 우선순위
    @Column("status")
    private String status;  // REGISTERED, ASSIGNED, IN_PROGRESS, etc.
    
    @Column("priority")
    private String priority;  // URGENT, HIGH, NORMAL, LOW
    
    // 담당자 정보
    @Column("counselor_id")
    private Long counselorId;  // 현재 담당 상담사
    
    @Column("assigned_by")
    private Long assignedBy;  // 할당한 사람
    
    @Column("original_counselor_id")
    private Long originalCounselorId;  // 최초 담당 상담사
    
    // 에스컬레이션 정보
    @Column("escalation_level")
    private Integer escalationLevel;  // 0: 일반, 1: 시니어, 2: 관리자, 3: 임원
    
    @Column("escalated_at")
    private LocalDateTime escalatedAt;
    
    @Column("escalated_by")
    private Long escalatedBy;
    
    @Column("escalation_reason")
    private String escalationReason;
    
    // 이관 정보
    @Column("transfer_count")
    private Integer transferCount;  // 이관 횟수
    
    @Column("last_transferred_at")
    private LocalDateTime lastTransferredAt;
    
    // SLA 정보
    @Column("sla_due_date")
    private LocalDateTime slaDueDate;  // 처리 기한
    
    @Column("first_response_at")
    private LocalDateTime firstResponseAt;  // 최초 응답 시간
    
    @Column("resolved_at")
    private LocalDateTime resolvedAt;  // 해결 시간
    
    // 해결 내용
    @Column("resolution")
    private String resolution;
    
    /**
     * 기본 생성자 (Spring Data JDBC가 객체를 생성할 때 필요)
     */
    Counseling() {
        // Spring Data JDBC가 조회 시 객체를 생성하기 위해 필요
    }
    
    @Builder
    public Counseling(String customerName, String contact, String counselingType,
                      String content, String status, Priority priority,
                      Long counselorId, Long assignedBy, Long originalCounselorId,
                      Integer escalationLevel, LocalDateTime escalatedAt, Long escalatedBy,
                      String escalationReason, Integer transferCount, LocalDateTime lastTransferredAt,
                      LocalDateTime slaDueDate, LocalDateTime firstResponseAt,
                      LocalDateTime resolvedAt, String resolution) {
        this.customerName = customerName;
        this.contact = contact;
        this.counselingType = counselingType;
        this.content = content;
        this.status = status != null ? status : "REGISTERED";
        this.priority = priority != null ? priority.name() : Priority.NORMAL.name();
        this.counselorId = counselorId;
        this.assignedBy = assignedBy;
        this.originalCounselorId = originalCounselorId;
        this.escalationLevel = escalationLevel != null ? escalationLevel : 0;
        this.escalatedAt = escalatedAt;
        this.escalatedBy = escalatedBy;
        this.escalationReason = escalationReason;
        this.transferCount = transferCount != null ? transferCount : 0;
        this.lastTransferredAt = lastTransferredAt;
        this.slaDueDate = slaDueDate;
        this.firstResponseAt = firstResponseAt;
        this.resolvedAt = resolvedAt;
        this.resolution = resolution;
        initializeTimestamps();
    }
    
    /**
     * ID 설정 (Repository에서 사용)
     */
    void setId(Long id) {
        this.id = id;
    }
    
    /**
     * 필드 설정 (Spring Data JDBC가 조회 시 사용)
     * package-private setter로 설정
     */
    void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    void setContact(String contact) {
        this.contact = contact;
    }
    
    void setCounselingType(String counselingType) {
        this.counselingType = counselingType;
    }
    
    void setContent(String content) {
        this.content = content;
    }
    
    void setStatus(String status) {
        this.status = status;
    }
    
    void setPriority(String priority) {
        this.priority = priority;
    }
    
    void setCounselorId(Long counselorId) {
        this.counselorId = counselorId;
    }
    
    void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }
    
    void setOriginalCounselorId(Long originalCounselorId) {
        this.originalCounselorId = originalCounselorId;
    }
    
    void setEscalationLevel(Integer escalationLevel) {
        this.escalationLevel = escalationLevel;
    }
    
    void setEscalatedAt(LocalDateTime escalatedAt) {
        this.escalatedAt = escalatedAt;
    }
    
    void setEscalatedBy(Long escalatedBy) {
        this.escalatedBy = escalatedBy;
    }
    
    void setEscalationReason(String escalationReason) {
        this.escalationReason = escalationReason;
    }
    
    void setTransferCount(Integer transferCount) {
        this.transferCount = transferCount;
    }
    
    void setLastTransferredAt(LocalDateTime lastTransferredAt) {
        this.lastTransferredAt = lastTransferredAt;
    }
    
    void setSlaDueDate(LocalDateTime slaDueDate) {
        this.slaDueDate = slaDueDate;
    }
    
    void setFirstResponseAt(LocalDateTime firstResponseAt) {
        this.firstResponseAt = firstResponseAt;
    }
    
    void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    void setResolution(String resolution) {
        this.resolution = resolution;
    }
    
    /**
     * 상태 Enum 조회
     * 주의: Spring Modulith 규칙상 internal에서 api를 직접 참조할 수 없으므로,
     * 이 메서드는 서비스 구현체에서만 사용됩니다.
     */
    public com.wrms.newtype.counseling.api.domain.CounselingStatus getStatusEnum() {
        return status != null ? com.wrms.newtype.counseling.api.domain.CounselingStatus.valueOf(status) : null;
    }
    
    /**
     * 우선순위 Enum 조회
     */
    public Priority getPriorityEnum() {
        return priority != null ? Priority.valueOf(priority) : null;
    }
    
    /**
     * 상태 변경
     */
    public void updateStatus(String newStatus) {
        this.status = newStatus;
        updateTimestamp();
        
        // 해결됨으로 변경 시 해결 시간 기록
        if ("RESOLVED".equals(newStatus) && this.resolvedAt == null) {
            this.resolvedAt = LocalDateTime.now();
        }
        
        // 최초 응답 시간 기록 (처리중으로 변경 시)
        if ("IN_PROGRESS".equals(newStatus) && this.firstResponseAt == null) {
            this.firstResponseAt = LocalDateTime.now();
        }
    }
    
    /**
     * 우선순위 변경
     */
    public void updatePriority(Priority newPriority) {
        this.priority = newPriority.name();
        updateTimestamp();
    }
    
    /**
     * 담당자 할당
     */
    public void assignCounselor(Long counselorId, Long assignedBy) {
        this.counselorId = counselorId;
        this.assignedBy = assignedBy;
        if (this.originalCounselorId == null) {
            this.originalCounselorId = counselorId;
        }
        updateTimestamp();
    }
    
    /**
     * 이관 처리
     */
    public void transfer(Long toCounselorId) {
        this.counselorId = toCounselorId;
        this.transferCount = (this.transferCount != null ? this.transferCount : 0) + 1;
        this.lastTransferredAt = LocalDateTime.now();
        this.status = "TRANSFERRED";
        updateTimestamp();
    }
    
    /**
     * 에스컬레이션 처리
     */
    public void escalate(int newLevel, Long escalatedBy, String reason, Long targetCounselorId) {
        this.escalationLevel = newLevel;
        this.escalatedAt = LocalDateTime.now();
        this.escalatedBy = escalatedBy;
        this.escalationReason = reason;
        this.counselorId = targetCounselorId;
        this.status = "ESCALATED";
        updateTimestamp();
    }
    
    /**
     * 해결 처리
     */
    public void resolve(String resolution) {
        this.resolution = resolution;
        this.resolvedAt = LocalDateTime.now();
        updateTimestamp();
    }
    
    /**
     * SLA 위반 여부 확인
     */
    public boolean isSlaViolated() {
        if (slaDueDate == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(slaDueDate) &&
               !"RESOLVED".equals(status) &&
               !"CLOSED".equals(status);
    }
    
    /**
     * 이관 가능 여부 확인
     */
    public boolean canTransfer() {
        return transferCount != null && transferCount < 3;
    }
}

