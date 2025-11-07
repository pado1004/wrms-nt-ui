package com.wrms.nt.domain.user.entity;

import com.wrms.nt.domain.common.entity.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 사용자(상담사/관리자) 엔티티
 */
@Table("user")
public class User {

    @Id
    private Long id;

    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String role;  // COUNSELOR, SENIOR_COUNSELOR, MANAGER, ADMIN
    private String department;
    private Boolean isActive;
    private Integer maxConcurrentCases;  // 동시 처리 가능한 최대 상담 수
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor
    public User() {
        this.isActive = true;
        this.maxConcurrentCases = 10;
    }

    public User(Long id, String username, String password, String name, String email,
                String phone, String role, String department, Boolean isActive,
                Integer maxConcurrentCases, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.department = department;
        this.isActive = isActive;
        this.maxConcurrentCases = maxConcurrentCases;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Role getRoleEnum() {
        return role != null ? Role.valueOf(role) : null;
    }

    public void setRoleEnum(Role role) {
        this.role = role != null ? role.name() : null;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getMaxConcurrentCases() {
        return maxConcurrentCases;
    }

    public void setMaxConcurrentCases(Integer maxConcurrentCases) {
        this.maxConcurrentCases = maxConcurrentCases;
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
}
