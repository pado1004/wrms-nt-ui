package com.wrms.nt.domain.user.service;

import com.wrms.nt.domain.common.entity.Role;
import com.wrms.nt.domain.user.entity.User;
import com.wrms.nt.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 서비스
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 모든 사용자 조회
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    /**
     * ID로 사용자 조회
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 사용자명으로 조회
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 이메일로 조회
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 역할별 사용자 조회
     */
    @Transactional(readOnly = true)
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role.name());
    }

    /**
     * 부서별 사용자 조회
     */
    @Transactional(readOnly = true)
    public List<User> findByDepartment(String department) {
        return userRepository.findByDepartment(department);
    }

    /**
     * 활성 사용자 조회
     */
    @Transactional(readOnly = true)
    public List<User> findActiveUsers() {
        return userRepository.findActiveUsers();
    }

    /**
     * 가용한 상담사 조회 (업무량 기준)
     */
    @Transactional(readOnly = true)
    public List<User> findAvailableCounselors() {
        return userRepository.findAvailableCounselors();
    }

    /**
     * 사용자 저장 (등록/수정)
     */
    public User save(User user) {
        if (user.getId() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * 사용자 생성
     */
    public User createUser(String username, String password, String name, String email,
                          String phone, Role role, String department) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);  // TODO: 비밀번호 암호화 필요
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRoleEnum(role);
        user.setDepartment(department);
        user.setIsActive(true);
        user.setMaxConcurrentCases(10);
        return save(user);
    }

    /**
     * 사용자 비활성화
     */
    public void deactivateUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setIsActive(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
    }

    /**
     * 사용자 활성화
     */
    public void activateUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setIsActive(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
    }

    /**
     * 사용자 삭제
     */
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
