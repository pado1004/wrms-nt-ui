package com.wrms.nt.domain.counseling.entity;

import com.wrms.nt.domain.common.entity.Priority;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 상담 엔티티
 */
@Table("counseling")
public class Counseling {

    @Id
    private Long id;

    // 기본 정보
    private String customerName;
    private String contact;
    private String counselingType;
    private String content;

    // 상태 및 우선순위
    private String status;  // REGISTERED, ASSIGNED, IN_PROGRESS, etc.
    private String priority;  // URGENT, HIGH, NORMAL, LOW

    // 담당자 정보
    private Long counselorId;  // 현재 담당 상담사
    private Long assignedBy;  // 할당한 사람
    private Long originalCounselorId;  // 최초 담당 상담사

    // 에스컬레이션 정보
    private Integer escalationLevel;  // 0: 일반, 1: 시니어, 2: 관리자, 3: 임원
    private LocalDateTime escalatedAt;
    private Long escalatedBy;
    private String escalationReason;

    // 이관 정보
    private Integer transferCount;  // 이관 횟수
    private LocalDateTime lastTransferredAt;

    // SLA 정보
    private LocalDateTime slaDueDate;  // 처리 기한
    private LocalDateTime firstResponseAt;  // 최초 응답 시간
    private LocalDateTime resolvedAt;  // 해결 시간

    // 해결 내용
    private String resolution;

    // 메타데이터
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor
    public Counseling() {
        this.status = CounselingStatus.REGISTERED.name();
        this.priority = Priority.NORMAL.name();
        this.escalationLevel = 0;
        this.transferCount = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCounselingType() {
        return counselingType;
    }

    public void setCounselingType(String counselingType) {
        this.counselingType = counselingType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CounselingStatus getStatusEnum() {
        return status != null ? CounselingStatus.valueOf(status) : null;
    }

    public void setStatusEnum(CounselingStatus status) {
        this.status = status != null ? status.name() : null;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Priority getPriorityEnum() {
        return priority != null ? Priority.valueOf(priority) : null;
    }

    public void setPriorityEnum(Priority priority) {
        this.priority = priority != null ? priority.name() : null;
    }

    public Long getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(Long counselorId) {
        this.counselorId = counselorId;
    }

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Long getOriginalCounselorId() {
        return originalCounselorId;
    }

    public void setOriginalCounselorId(Long originalCounselorId) {
        this.originalCounselorId = originalCounselorId;
    }

    public Integer getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(Integer escalationLevel) {
        this.escalationLevel = escalationLevel;
    }

    public LocalDateTime getEscalatedAt() {
        return escalatedAt;
    }

    public void setEscalatedAt(LocalDateTime escalatedAt) {
        this.escalatedAt = escalatedAt;
    }

    public Long getEscalatedBy() {
        return escalatedBy;
    }

    public void setEscalatedBy(Long escalatedBy) {
        this.escalatedBy = escalatedBy;
    }

    public String getEscalationReason() {
        return escalationReason;
    }

    public void setEscalationReason(String escalationReason) {
        this.escalationReason = escalationReason;
    }

    public Integer getTransferCount() {
        return transferCount;
    }

    public void setTransferCount(Integer transferCount) {
        this.transferCount = transferCount;
    }

    public LocalDateTime getLastTransferredAt() {
        return lastTransferredAt;
    }

    public void setLastTransferredAt(LocalDateTime lastTransferredAt) {
        this.lastTransferredAt = lastTransferredAt;
    }

    public LocalDateTime getSlaDueDate() {
        return slaDueDate;
    }

    public void setSlaDueDate(LocalDateTime slaDueDate) {
        this.slaDueDate = slaDueDate;
    }

    public LocalDateTime getFirstResponseAt() {
        return firstResponseAt;
    }

    public void setFirstResponseAt(LocalDateTime firstResponseAt) {
        this.firstResponseAt = firstResponseAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * SLA 위반 여부 확인
     */
    public boolean isSlaViolated() {
        if (slaDueDate == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(slaDueDate) &&
               !CounselingStatus.RESOLVED.name().equals(status) &&
               !CounselingStatus.CLOSED.name().equals(status);
    }

    /**
     * 이관 가능 여부 확인
     */
    public boolean canTransfer() {
        return transferCount != null && transferCount < 3;
    }
}
