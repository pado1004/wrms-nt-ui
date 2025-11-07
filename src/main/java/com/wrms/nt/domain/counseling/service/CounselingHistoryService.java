package com.wrms.nt.domain.counseling.service;

import com.wrms.nt.domain.counseling.entity.ActionType;
import com.wrms.nt.domain.counseling.entity.CounselingHistory;
import com.wrms.nt.domain.counseling.repository.CounselingHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 상담 이력 서비스
 */
@Service
@Transactional
public class CounselingHistoryService {

    private final CounselingHistoryRepository historyRepository;

    public CounselingHistoryService(CounselingHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * 상담 ID로 이력 조회
     */
    @Transactional(readOnly = true)
    public List<CounselingHistory> findByCounselingId(Long counselingId) {
        return historyRepository.findByCounselingIdOrderByPerformedAtDesc(counselingId);
    }

    /**
     * 상담 ID와 액션 타입으로 조회
     */
    @Transactional(readOnly = true)
    public List<CounselingHistory> findByCounselingIdAndActionType(Long counselingId, ActionType actionType) {
        return historyRepository.findByCounselingIdAndActionType(counselingId, actionType.name());
    }

    /**
     * 사용자가 수행한 이력 조회
     */
    @Transactional(readOnly = true)
    public List<CounselingHistory> findByPerformedBy(Long userId) {
        return historyRepository.findByPerformedBy(userId);
    }

    /**
     * 이력 생성
     */
    public CounselingHistory createHistory(Long counselingId, ActionType actionType, Long performedBy) {
        CounselingHistory history = new CounselingHistory();
        history.setCounselingId(counselingId);
        history.setActionTypeEnum(actionType);
        history.setPerformedBy(performedBy);
        history.setPerformedAt(LocalDateTime.now());
        return historyRepository.save(history);
    }

    /**
     * 상태 변경 이력 생성
     */
    public CounselingHistory createStatusChangeHistory(
            Long counselingId, String fromStatus, String toStatus, Long performedBy) {
        CounselingHistory history = new CounselingHistory();
        history.setCounselingId(counselingId);
        history.setActionTypeEnum(ActionType.STATUS_CHANGED);
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setPerformedBy(performedBy);
        history.setPerformedAt(LocalDateTime.now());
        return historyRepository.save(history);
    }

    /**
     * 이관 이력 생성
     */
    public CounselingHistory createTransferHistory(
            Long counselingId, Long fromUserId, Long toUserId, String reason, String comment, Long performedBy) {
        CounselingHistory history = new CounselingHistory();
        history.setCounselingId(counselingId);
        history.setActionTypeEnum(ActionType.TRANSFERRED);
        history.setFromUserId(fromUserId);
        history.setToUserId(toUserId);
        history.setReason(reason);
        history.setComment(comment);
        history.setPerformedBy(performedBy);
        history.setPerformedAt(LocalDateTime.now());
        return historyRepository.save(history);
    }

    /**
     * 에스컬레이션 이력 생성
     */
    public CounselingHistory createEscalationHistory(
            Long counselingId, Long fromUserId, Long toUserId, String reason, String comment, Long performedBy) {
        CounselingHistory history = new CounselingHistory();
        history.setCounselingId(counselingId);
        history.setActionTypeEnum(ActionType.ESCALATED);
        history.setFromUserId(fromUserId);
        history.setToUserId(toUserId);
        history.setReason(reason);
        history.setComment(comment);
        history.setPerformedBy(performedBy);
        history.setPerformedAt(LocalDateTime.now());
        return historyRepository.save(history);
    }

    /**
     * 코멘트 이력 생성
     */
    public CounselingHistory createCommentHistory(Long counselingId, String comment, Long performedBy) {
        CounselingHistory history = new CounselingHistory();
        history.setCounselingId(counselingId);
        history.setActionTypeEnum(ActionType.COMMENTED);
        history.setComment(comment);
        history.setPerformedBy(performedBy);
        history.setPerformedAt(LocalDateTime.now());
        return historyRepository.save(history);
    }
}
