package com.wrms.newtype.user.internal.domain;

import com.wrms.newtype.shared.base.BaseEntity;
import com.wrms.newtype.shared.domain.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 사용자(상담사/관리자) 엔티티
 */
@Getter
@Table("user")
public class User extends BaseEntity {
    
    @Id
    @Column("id")
    private Long id;
    
    @Column("username")
    private String username;
    
    @Column("password")
    private String password;
    
    @Column("name")
    private String name;
    
    @Column("email")
    private String email;
    
    @Column("phone")
    private String phone;
    
    @Column("role")
    private String role;  // COUNSELOR, SENIOR_COUNSELOR, MANAGER, ADMIN
    
    @Column("department")
    private String department;
    
    @Column("is_active")
    private Boolean isActive;
    
    @Column("max_concurrent_cases")
    private Integer maxConcurrentCases;  // 동시 처리 가능한 최대 상담 수
    
    /**
     * 기본 생성자 (Spring Data JDBC가 객체를 생성할 때 필요)
     */
    User() {
        // Spring Data JDBC가 조회 시 객체를 생성하기 위해 필요
    }
    
    @Builder
    public User(String username, String password, String name, String email,
                String phone, Role role, String department, Boolean isActive,
                Integer maxConcurrentCases) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role != null ? role.name() : null;
        this.department = department;
        this.isActive = isActive != null ? isActive : true;
        this.maxConcurrentCases = maxConcurrentCases != null ? maxConcurrentCases : 10;
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
    void setUsername(String username) {
        this.username = username;
    }
    
    void setPassword(String password) {
        this.password = password;
    }
    
    void setName(String name) {
        this.name = name;
    }
    
    void setEmail(String email) {
        this.email = email;
    }
    
    void setPhone(String phone) {
        this.phone = phone;
    }
    
    void setRole(String role) {
        this.role = role;
    }
    
    void setDepartment(String department) {
        this.department = department;
    }
    
    void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    void setMaxConcurrentCases(Integer maxConcurrentCases) {
        this.maxConcurrentCases = maxConcurrentCases;
    }
    
    /**
     * 역할 Enum 조회
     */
    public Role getRoleEnum() {
        return role != null ? Role.valueOf(role) : null;
    }
    
    /**
     * 사용자 비활성화
     */
    public void deactivate() {
        this.isActive = false;
        updateTimestamp();
    }
    
    /**
     * 사용자 활성화
     */
    public void activate() {
        this.isActive = true;
        updateTimestamp();
    }
}

