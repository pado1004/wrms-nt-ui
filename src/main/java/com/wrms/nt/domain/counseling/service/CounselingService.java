package com.wrms.nt.domain.counseling.service;

import com.wrms.nt.domain.common.entity.Priority;
import com.wrms.nt.domain.common.entity.Role;
import com.wrms.nt.domain.counseling.entity.*;
import com.wrms.nt.domain.counseling.repository.CounselingRepository;
import com.wrms.nt.domain.user.entity.User;
import com.wrms.nt.domain.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 상담 서비스
 */
@Service
@Transactional
public class CounselingService {

    private final CounselingRepository counselingRepository;
    private final CounselingHistoryService historyService;
    private final UserService userService;

    public CounselingService(CounselingRepository counselingRepository,
                           CounselingHistoryService historyService,
                           UserService userService) {
        this.counselingRepository = counselingRepository;
        this.historyService = historyService;
        this.userService = userService;
    }

    /**
     * 모든 상담 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Counseling> findAll() {
        return counselingRepository.findAllOrderByCreatedAtDesc();
    }

    /**
     * 상담 ID로 조회
     */
    @Transactional(readOnly = true)
    public Optional<Counseling> findById(Long id) {
        return counselingRepository.findById(id);
    }

    /**
     * 상태별 상담 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Counseling> findByStatus(String status) {
        return counselingRepository.findByStatus(status);
    }

    /**
     * 상담사별 상담 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Counseling> findByCounselorId(Long counselorId) {
        return counselingRepository.findByCounselorId(counselorId);
    }

    /**
     * 고객명으로 상담 검색
     */
    @Transactional(readOnly = true)
    public List<Counseling> searchByCustomerName(String customerName) {
        return counselingRepository.findByCustomerNameContaining("%" + customerName + "%");
    }

    /**
     * 상담 저장 (등록/수정)
     */
    public Counseling save(Counseling counseling) {
        if (counseling.getId() == null) {
            counseling.setCreatedAt(LocalDateTime.now());
        }
        counseling.setUpdatedAt(LocalDateTime.now());
        return counselingRepository.save(counseling);
    }

    /**
     * 상담 생성 (자동 할당 포함)
     */
    public Counseling createCounseling(String customerName, String contact, String counselingType,
                                      String content, Priority priority, Long createdBy) {
        Counseling counseling = new Counseling();
        counseling.setCustomerName(customerName);
        counseling.setContact(contact);
        counseling.setCounselingType(counselingType);
        counseling.setContent(content);
        counseling.setPriorityEnum(priority);
        counseling.setStatusEnum(CounselingStatus.REGISTERED);

        // SLA 기한 계산
        LocalDateTime slaDueDate = LocalDateTime.now().plusHours(priority.getSlaHours());
        counseling.setSlaDueDate(slaDueDate);

        // 저장
        counseling = save(counseling);

        // 이력 기록: 생성
        historyService.createHistory(counseling.getId(), ActionType.CREATED, createdBy);

        // 자동 할당
        assignToAvailableCounselor(counseling.getId(), createdBy);

        return counseling;
    }

    /**
     * 가용한 상담사에게 자동 할당
     */
    public Counseling assignToAvailableCounselor(Long counselingId, Long assignedBy) {
        Counseling counseling = findById(counselingId)
            .orElseThrow(() -> new IllegalArgumentException("Counseling not found with id: " + counselingId));

        // 가용한 상담사 찾기
        List<User> availableCounselors = userService.findAvailableCounselors();
        if (availableCounselors.isEmpty()) {
            throw new IllegalStateException("No available counselors");
        }

        // 첫 번째 가용 상담사에게 할당
        User counselor = availableCounselors.get(0);
        counseling.setCounselorId(counselor.getId());
        counseling.setAssignedBy(assignedBy);

        // 최초 담당자 기록
        if (counseling.getOriginalCounselorId() == null) {
            counseling.setOriginalCounselorId(counselor.getId());
        }

        counseling.setStatusEnum(CounselingStatus.ASSIGNED);
        counseling = save(counseling);

        // 이력 기록: 할당
        historyService.createHistory(counseling.getId(), ActionType.ASSIGNED, assignedBy);

        return counseling;
    }

    /**
     * 상담 상태 변경
     */
    public Counseling updateStatus(Long id, CounselingStatus newStatus, Long performedBy) {
        Counseling counseling = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Counseling not found with id: " + id));

        String oldStatus = counseling.getStatus();
        counseling.setStatusEnum(newStatus);

        // 해결됨으로 변경 시 해결 시간 기록
        if (newStatus == CounselingStatus.RESOLVED && counseling.getResolvedAt() == null) {
            counseling.setResolvedAt(LocalDateTime.now());
        }

        // 최초 응답 시간 기록 (처리중으로 변경 시)
        if (newStatus == CounselingStatus.IN_PROGRESS && counseling.getFirstResponseAt() == null) {
            counseling.setFirstResponseAt(LocalDateTime.now());
        }

        counseling = save(counseling);

        // 이력 기록: 상태 변경
        historyService.createStatusChangeHistory(id, oldStatus, newStatus.name(), performedBy);

        return counseling;
    }

    /**
     * 상담 삭제
     */
    public void delete(Long id) {
        counselingRepository.deleteById(id);
    }

    /**
     * 상담 이관
     */
    public Counseling transferCounseling(Long counselingId, Long toCounselorId,
                                        TransferReason reason, String comment, Long performedBy) {
        Counseling counseling = findById(counselingId)
            .orElseThrow(() -> new IllegalArgumentException("Counseling not found with id: " + counselingId));

        // 이관 가능 여부 확인
        if (!counseling.canTransfer()) {
            // 이관 횟수 초과 시 자동 에스컬레이션
            return escalateCounseling(counselingId, "이관 횟수 초과 (3회)", comment, performedBy);
        }

        Long fromCounselorId = counseling.getCounselorId();

        // 이관 정보 업데이트
        counseling.setCounselorId(toCounselorId);
        counseling.setTransferCount(counseling.getTransferCount() + 1);
        counseling.setLastTransferredAt(LocalDateTime.now());
        counseling.setStatusEnum(CounselingStatus.TRANSFERRED);
        counseling = save(counseling);

        // 이력 기록: 이관
        historyService.createTransferHistory(
            counselingId, fromCounselorId, toCounselorId,
            reason.name(), comment, performedBy
        );

        // 이관 후 자동으로 ASSIGNED 상태로 변경
        updateStatus(counselingId, CounselingStatus.ASSIGNED, toCounselorId);

        return counseling;
    }

    /**
     * 상담 에스컬레이션
     */
    public Counseling escalateCounseling(Long counselingId, String reason, String comment, Long performedBy) {
        Counseling counseling = findById(counselingId)
            .orElseThrow(() -> new IllegalArgumentException("Counseling not found with id: " + counselingId));

        Long fromUserId = counseling.getCounselorId();
        int currentLevel = counseling.getEscalationLevel();
        int newLevel = currentLevel + 1;

        // 에스컬레이션 대상 역할 결정
        Role targetRole = determineEscalationRole(newLevel);

        // 해당 역할의 사용자 찾기
        List<User> targetUsers = userService.findByRole(targetRole);
        if (targetUsers.isEmpty()) {
            throw new IllegalStateException("No available users with role: " + targetRole);
        }

        // 첫 번째 사용자에게 할당
        User targetUser = targetUsers.get(0);

        // 에스컬레이션 정보 업데이트
        counseling.setEscalationLevel(newLevel);
        counseling.setEscalatedAt(LocalDateTime.now());
        counseling.setEscalatedBy(performedBy);
        counseling.setEscalationReason(reason);
        counseling.setCounselorId(targetUser.getId());
        counseling.setStatusEnum(CounselingStatus.ESCALATED);

        // 우선순위 자동 상향 (NORMAL -> HIGH, HIGH -> URGENT)
        if (counseling.getPriorityEnum() == Priority.NORMAL) {
            counseling.setPriorityEnum(Priority.HIGH);
        } else if (counseling.getPriorityEnum() == Priority.HIGH) {
            counseling.setPriorityEnum(Priority.URGENT);
        }

        counseling = save(counseling);

        // 이력 기록: 에스컬레이션
        historyService.createEscalationHistory(
            counselingId, fromUserId, targetUser.getId(),
            reason, comment, performedBy
        );

        return counseling;
    }

    /**
     * 에스컬레이션 레벨에 따른 대상 역할 결정
     */
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
     * 상담 해결
     */
    public Counseling resolveCounseling(Long counselingId, String resolution, Long performedBy) {
        Counseling counseling = findById(counselingId)
            .orElseThrow(() -> new IllegalArgumentException("Counseling not found with id: " + counselingId));

        counseling.setResolution(resolution);
        counseling.setResolvedAt(LocalDateTime.now());
        counseling.setStatusEnum(CounselingStatus.RESOLVED);
        counseling = save(counseling);

        // 이력 기록: 해결
        historyService.createHistory(counselingId, ActionType.RESOLVED, performedBy);

        return counseling;
    }

    /**
     * 상담 종료
     */
    public Counseling closeCounseling(Long counselingId, Long performedBy) {
        Counseling counseling = findById(counselingId)
            .orElseThrow(() -> new IllegalArgumentException("Counseling not found with id: " + counselingId));

        counseling.setStatusEnum(CounselingStatus.CLOSED);
        counseling = save(counseling);

        // 이력 기록: 종료
        historyService.createHistory(counselingId, ActionType.CLOSED, performedBy);

        return counseling;
    }

    /**
     * 코멘트 추가
     */
    public void addComment(Long counselingId, String comment, Long performedBy) {
        Counseling counseling = findById(counselingId)
            .orElseThrow(() -> new IllegalArgumentException("Counseling not found with id: " + counselingId));

        // 이력 기록: 코멘트
        historyService.createCommentHistory(counselingId, comment, performedBy);
    }

    /**
     * SLA 위반 상담 조회
     */
    @Transactional(readOnly = true)
    public List<Counseling> findSlaViolatedCounselings() {
        List<Counseling> allCounselings = findAll();
        return allCounselings.stream()
            .filter(Counseling::isSlaViolated)
            .toList();
    }
}
