package com.wrms.nt.domain.user.repository;

import com.wrms.nt.domain.user.entity.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 리포지토리
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * 사용자명으로 조회
     */
    @Query("SELECT * FROM user WHERE username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * 이메일로 조회
     */
    @Query("SELECT * FROM user WHERE email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * 역할별 사용자 조회
     */
    @Query("SELECT * FROM user WHERE role = :role AND is_active = TRUE ORDER BY name")
    List<User> findByRole(@Param("role") String role);

    /**
     * 부서별 사용자 조회
     */
    @Query("SELECT * FROM user WHERE department = :department AND is_active = TRUE ORDER BY name")
    List<User> findByDepartment(@Param("department") String department);

    /**
     * 활성 사용자 조회
     */
    @Query("SELECT * FROM user WHERE is_active = TRUE ORDER BY name")
    List<User> findActiveUsers();

    /**
     * 상담사 목록 조회 (업무량이 적은 순)
     */
    @Query("SELECT u.* FROM user u " +
           "LEFT JOIN counseling c ON u.id = c.counselor_id AND c.status IN ('ASSIGNED', 'IN_PROGRESS') " +
           "WHERE u.role IN ('COUNSELOR', 'SENIOR_COUNSELOR') AND u.is_active = TRUE " +
           "GROUP BY u.id " +
           "HAVING COUNT(c.id) < u.max_concurrent_cases " +
           "ORDER BY COUNT(c.id)")
    List<User> findAvailableCounselors();
}
