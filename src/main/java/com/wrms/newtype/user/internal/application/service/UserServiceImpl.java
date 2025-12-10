package com.wrms.newtype.user.internal.application.service;

import com.wrms.newtype.shared.domain.Role;
import com.wrms.newtype.user.api.dto.response.UserResponse;
import com.wrms.newtype.user.api.service.UserQueryService;
import com.wrms.newtype.user.internal.domain.User;
import com.wrms.newtype.user.internal.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 사용자 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserQueryService {
    
    private final UserRepository userRepository;
    
    @Override
    public Optional<UserResponse> findById(Long id) {
        return userRepository.findById(id)
            .map(UserResponse::from);
    }
    
    @Override
    public Optional<UserResponse> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(UserResponse::from);
    }
    
    @Override
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(UserResponse::from);
    }
    
    @Override
    public List<UserResponse> findByRole(Role role) {
        return userRepository.findByRole(role.name()).stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserResponse> findByDepartment(String department) {
        return userRepository.findByDepartment(department).stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserResponse> findActiveUsers() {
        return userRepository.findActiveUsers().stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserResponse> findAvailableCounselors() {
        return userRepository.findAvailableCounselors().stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 사용자 조회 (내부용 - 엔티티 반환)
     * TODO: counseling 모듈에서 필요하므로 임시로 제공
     */
    public Optional<User> findEntityById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * 역할별 사용자 조회 (내부용 - 엔티티 반환)
     */
    public List<User> findEntityByRole(Role role) {
        return userRepository.findByRole(role.name());
    }
    
    /**
     * 활성 사용자 조회 (내부용 - 엔티티 반환)
     */
    public List<User> findEntityActiveUsers() {
        return userRepository.findActiveUsers();
    }
    
    /**
     * 가용한 상담사 조회 (내부용 - 엔티티 반환)
     */
    public List<User> findEntityAvailableCounselors() {
        return userRepository.findAvailableCounselors();
    }
}

