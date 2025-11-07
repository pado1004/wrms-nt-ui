package com.wrms.nt.domain.counseling.service;

import com.wrms.nt.domain.counseling.entity.Counseling;
import com.wrms.nt.domain.counseling.repository.CounselingRepository;
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

    public CounselingService(CounselingRepository counselingRepository) {
        this.counselingRepository = counselingRepository;
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
     * 상담 삭제
     */
    public void delete(Long id) {
        counselingRepository.deleteById(id);
    }

    /**
     * 상담 상태 변경
     */
    public Counseling updateStatus(Long id, String status) {
        Optional<Counseling> optionalCounseling = counselingRepository.findById(id);
        if (optionalCounseling.isPresent()) {
            Counseling counseling = optionalCounseling.get();
            counseling.setStatus(status);
            counseling.setUpdatedAt(LocalDateTime.now());
            return counselingRepository.save(counseling);
        }
        throw new IllegalArgumentException("Counseling not found with id: " + id);
    }

}
