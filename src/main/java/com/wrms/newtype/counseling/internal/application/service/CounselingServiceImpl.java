package com.wrms.newtype.counseling.internal.application.service;

import com.wrms.newtype.counseling.api.domain.ActionType;
import com.wrms.newtype.counseling.api.domain.CounselingStatus;
import com.wrms.newtype.counseling.api.dto.request.*;
import com.wrms.newtype.counseling.api.dto.response.CounselingResponse;
import com.wrms.newtype.counseling.api.event.*;
import com.wrms.newtype.counseling.api.exception.CounselingNotFoundException;
import com.wrms.newtype.counseling.api.service.CounselingCommandService;
import com.wrms.newtype.counseling.api.service.CounselingQueryService;
import com.wrms.newtype.counseling.internal.domain.Counseling;
import com.wrms.newtype.counseling.internal.domain.CounselingHistory;
import com.wrms.newtype.counseling.internal.infrastructure.persistence.CounselingHistoryRepository;
import com.wrms.newtype.counseling.internal.infrastructure.persistence.CounselingRepository;
import com.wrms.newtype.shared.domain.Priority;
import com.wrms.newtype.shared.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 상담 서비스 구현체
 * Command와 Query를 모두 구현합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CounselingServiceImpl implements CounselingCommandService, CounselingQueryService {
    
    private final CounselingRepository counselingRepository;
    private final CounselingHistoryRepository historyRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    // TODO: 다른 모듈 재구성 후 API 인터페이스로 변경
    // 현재는 임시로 직접 의존성 주입 (나중에 제거 예정)
    // private final com.wrms.newtype.user.api.service.UserQueryService userQueryService;
    // private final com.wrms.newtype.notification.api.service.NotificationCommandService notificationCommandService;
    
    @Override
    public CounselingResponse create(CreateCounselingRequest request) {
        // SLA 기한 계산
        LocalDateTime slaDueDate = LocalDateTime.now().plusHours(request.priority().getSlaHours());
        
        // 엔티티 생성
        Counseling counseling = Counseling.builder()
            .customerName(request.customerName())
            .contact(request.contact())
            .counselingType(request.counselingType())
            .content(request.content())
            .status("REGISTERED")
            .priority(request.priority())
            .slaDueDate(slaDueDate)
            .build();
        
        Counseling saved = counselingRepository.save(counseling);
        
        // 이력 기록: 생성
        createHistory(saved.getId(), ActionType.CREATED, request.createdBy());
        
        // 이벤트 발행
        eventPublisher.publishEvent(new CounselingCreatedEvent(
            saved.getId(),
            saved.getCustomerName(),
            saved.getCounselorId()
        ));
        
        // 자동 할당 (TODO: 이벤트 리스너로 이동 예정)
        // assignToAvailableCounselor(saved.getId(), request.createdBy());
        
        return convertToResponse(saved);
    }
    
    /**
     * 엔티티를 Response DTO로 변환
     */
    private CounselingResponse convertToResponse(Counseling entity) {
        return new CounselingResponse(
            entity.getId(),
            entity.getCustomerName(),
            entity.getContact(),
            entity.getCounselingType(),
            entity.getContent(),
            entity.getStatusEnum(),
            entity.getPriorityEnum(),
            entity.getCounselorId(),
            entity.getAssignedBy(),
            entity.getOriginalCounselorId(),
            entity.getEscalationLevel(),
            entity.getEscalatedAt(),
            entity.getEscalatedBy(),
            entity.getEscalationReason(),
            entity.getTransferCount(),
            entity.getLastTransferredAt(),
            entity.getSlaDueDate(),
            entity.getFirstResponseAt(),
            entity.getResolvedAt(),
            entity.getResolution(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public CounselingResponse findById(Long id) {
        Counseling counseling = counselingRepository.findById(id)
            .orElseThrow(() -> new CounselingNotFoundException(id));
        return convertToResponse(counseling);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CounselingResponse> findAll() {
        return counselingRepository.findAllOrderByCreatedAtDesc().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CounselingResponse> findByStatus(String status) {
        return counselingRepository.findByStatus(status).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CounselingResponse> findByCounselorId(Long counselorId) {
        return counselingRepository.findByCounselorId(counselorId).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CounselingResponse> searchByCustomerName(String customerName) {
        return counselingRepository.findByCustomerNameContaining("%" + customerName + "%").stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CounselingResponse> findSlaViolatedCounselings() {
        return counselingRepository.findSlaViolatedCounselings().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<com.wrms.newtype.counseling.api.dto.response.CounselingHistoryResponse> findHistoryByCounselingId(Long counselingId) {
        return historyRepository.findByCounselingIdOrderByPerformedAtDesc(counselingId).stream()
            .map(history -> {
                ActionType actionType = history.getActionTypeEnum();
                return com.wrms.newtype.counseling.api.dto.response.CounselingHistoryResponse.of(
                    history.getId(),
                    history.getCounselingId(),
                    actionType,
                    history.getFromStatus(),
                    history.getToStatus(),
                    history.getFromUserId(),
                    history.getToUserId(),
                    history.getComment(),
                    history.getReason(),
                    history.getPerformedBy(),
                    history.getPerformedAt()
                );
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public void updateStatus(Long id, UpdateCounselingStatusRequest request) {
        Counseling counseling = findCounselingById(id);
        
        String oldStatus = counseling.getStatus();
        counseling.updateStatus(request.status().name());
        
        Counseling saved = counselingRepository.save(counseling);
        
        // 이력 기록: 상태 변경
        createStatusChangeHistory(id, oldStatus, request.status().name(), request.performedBy());
        
        // 이벤트 발행
        eventPublisher.publishEvent(new CounselingAssignedEvent(
            saved.getId(),
            saved.getCounselorId(),
            request.performedBy()
        ));
    }
    
    @Override
    public CounselingResponse transfer(Long id, TransferCounselingRequest request) {
        Counseling counseling = findCounselingById(id);
        
        // 이관 가능 여부 확인
        if (!counseling.canTransfer()) {
            // 이관 횟수 초과 시 자동 에스컬레이션
            EscalateCounselingRequest escalateRequest = new EscalateCounselingRequest(
                "이관 횟수 초과 (3회)",
                request.comment(),
                request.performedBy()
            );
            return escalate(id, escalateRequest);
        }
        
        Long fromCounselorId = counseling.getCounselorId();
        
        // 이관 처리
        counseling.transfer(request.toCounselorId());
        
        Counseling saved = counselingRepository.save(counseling);
        
        // 이력 기록: 이관
        createTransferHistory(
            id, fromCounselorId, request.toCounselorId(),
            request.reason().name(), request.comment(), request.performedBy()
        );
        
        // 이벤트 발행
        eventPublisher.publishEvent(new CounselingTransferredEvent(
            saved.getId(),
            fromCounselorId,
            request.toCounselorId(),
            request.reason().name()
        ));
        
        // 이관 후 자동으로 ASSIGNED 상태로 변경
        updateStatus(id, new UpdateCounselingStatusRequest(CounselingStatus.ASSIGNED, request.toCounselorId()));
        
        return convertToResponse(saved);
    }
    
    @Override
    public CounselingResponse escalate(Long id, EscalateCounselingRequest request) {
        Counseling counseling = findCounselingById(id);
        
        Long fromUserId = counseling.getCounselorId();
        int currentLevel = counseling.getEscalationLevel() != null ? counseling.getEscalationLevel() : 0;
        int newLevel = currentLevel + 1;
        
        // 에스컬레이션 대상 역할 결정
        // TODO: 다른 모듈 재구성 후 API 인터페이스로 변경
        // Role targetRole = determineEscalationRole(newLevel);
        // List<User> targetUsers = userQueryService.findByRole(targetRole);
        // if (targetUsers.isEmpty()) {
        //     throw new IllegalStateException("No available users with role: " + targetRole);
        // }
        // User targetUser = targetUsers.get(0);
        
        // TODO: 다른 모듈 재구성 후 API 인터페이스로 변경
        // 임시 처리: 에스컬레이션만 수행 (담당자 할당은 나중에)
        Long targetUserId = fromUserId; // 임시
        
        // 에스컬레이션 처리
        counseling.escalate(newLevel, request.performedBy(), request.reason(), targetUserId);
        
        // 우선순위 자동 상향
        Priority currentPriority = counseling.getPriorityEnum();
        if (currentPriority == Priority.NORMAL) {
            counseling.updatePriority(Priority.HIGH);
        } else if (currentPriority == Priority.HIGH) {
            counseling.updatePriority(Priority.URGENT);
        }
        
        Counseling saved = counselingRepository.save(counseling);
        
        // 이력 기록: 에스컬레이션
        createEscalationHistory(
            id, fromUserId, targetUserId,
            request.reason(), request.comment(), request.performedBy()
        );
        
        // 이벤트 발행
        eventPublisher.publishEvent(new CounselingEscalatedEvent(
            saved.getId(),
            newLevel,
            request.performedBy(),
            request.reason()
        ));
        
        return convertToResponse(saved);
    }
    
    @Override
    public CounselingResponse resolve(Long id, ResolveCounselingRequest request) {
        Counseling counseling = findCounselingById(id);
        
        counseling.resolve(request.resolution());
        counseling.updateStatus("RESOLVED");
        
        Counseling saved = counselingRepository.save(counseling);
        
        // 이력 기록: 해결
        createHistory(id, ActionType.RESOLVED, request.performedBy());
        
        // 이벤트 발행
        eventPublisher.publishEvent(new CounselingResolvedEvent(
            saved.getId(),
            request.performedBy()
        ));
        
        return convertToResponse(saved);
    }
    
    @Override
    public CounselingResponse close(Long id, Long performedBy) {
        Counseling counseling = findCounselingById(id);
        
        counseling.updateStatus("CLOSED");
        
        Counseling saved = counselingRepository.save(counseling);
        
        // 이력 기록: 종료
        createHistory(id, ActionType.CLOSED, performedBy);
        
        return convertToResponse(saved);
    }
    
    @Override
    public void addComment(Long id, AddCommentRequest request) {
        findCounselingById(id);
        
        // 이력 기록: 코멘트
        createCommentHistory(id, request.comment(), request.performedBy());
        
        // TODO: 알림 생성은 이벤트 리스너로 이동 예정
    }
    
    @Override
    public void delete(Long id) {
        findCounselingById(id);
        counselingRepository.deleteById(id);
    }
    
    /**
     * 상담 조회 (내부용)
     */
    private Counseling findCounselingById(Long id) {
        return counselingRepository.findById(id)
            .orElseThrow(() -> new CounselingNotFoundException(id));
    }
    
    /**
     * 에스컬레이션 레벨에 따른 대상 역할 결정
     * TODO: 다른 모듈 재구성 후 사용 예정
     */
    @SuppressWarnings("unused")
    private Role determineEscalationRole(int escalationLevel) {
        switch (escalationLevel) {
            case 1:
                return Role.SENIOR_COUNSELOR;
            case 2:
                return Role.MANAGER;
            case 3:
            default:
                return Role.ADMIN;
        }
    }
    
    /**
     * 이력 생성 헬퍼 메서드
     */
    private CounselingHistory createHistory(Long counselingId, ActionType actionType, Long performedBy) {
        CounselingHistory history = CounselingHistory.builder()
            .counselingId(counselingId)
            .actionType(actionType.name())
            .performedBy(performedBy)
            .build();
        return historyRepository.save(history);
    }
    
    /**
     * 상태 변경 이력 생성
     */
    private void createStatusChangeHistory(Long counselingId, String fromStatus, String toStatus, Long performedBy) {
        CounselingHistory history = CounselingHistory.builder()
            .counselingId(counselingId)
            .actionType(ActionType.STATUS_CHANGED.name())
            .fromStatus(fromStatus)
            .toStatus(toStatus)
            .performedBy(performedBy)
            .build();
        historyRepository.save(history);
    }
    
    /**
     * 이관 이력 생성
     */
    private void createTransferHistory(Long counselingId, Long fromUserId, Long toUserId,
                                       String reason, String comment, Long performedBy) {
        CounselingHistory history = CounselingHistory.builder()
            .counselingId(counselingId)
            .actionType(ActionType.TRANSFERRED.name())
            .fromUserId(fromUserId)
            .toUserId(toUserId)
            .reason(reason)
            .comment(comment)
            .performedBy(performedBy)
            .build();
        historyRepository.save(history);
    }
    
    /**
     * 에스컬레이션 이력 생성
     */
    private void createEscalationHistory(Long counselingId, Long fromUserId, Long toUserId,
                                         String reason, String comment, Long performedBy) {
        CounselingHistory history = CounselingHistory.builder()
            .counselingId(counselingId)
            .actionType(ActionType.ESCALATED.name())
            .fromUserId(fromUserId)
            .toUserId(toUserId)
            .reason(reason)
            .comment(comment)
            .performedBy(performedBy)
            .build();
        historyRepository.save(history);
    }
    
    /**
     * 코멘트 이력 생성
     */
    private void createCommentHistory(Long counselingId, String comment, Long performedBy) {
        CounselingHistory history = CounselingHistory.builder()
            .counselingId(counselingId)
            .actionType(ActionType.COMMENTED.name())
            .comment(comment)
            .performedBy(performedBy)
            .build();
        historyRepository.save(history);
    }
}

