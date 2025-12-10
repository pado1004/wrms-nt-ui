package com.wrms.newtype.customer.internal.domain;

import com.wrms.newtype.shared.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 고객 엔티티
 */
@Getter
@Table("customer")
public class Customer extends BaseEntity {
    
    @Id
    @Column("id")
    private Long id;
    
    @Column("name")
    private String name;
    
    @Column("email")
    private String email;
    
    @Column("phone")
    private String phone;
    
    @Column("address")
    private String address;
    
    /**
     * 기본 생성자 (Spring Data JDBC가 객체를 생성할 때 필요)
     */
    Customer() {
        // Spring Data JDBC가 조회 시 객체를 생성하기 위해 필요
    }
    
    @Builder
    public Customer(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
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
    void setName(String name) {
        this.name = name;
    }
    
    void setEmail(String email) {
        this.email = email;
    }
    
    void setPhone(String phone) {
        this.phone = phone;
    }
    
    void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * 고객 정보 업데이트
     */
    public void update(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        updateTimestamp();
    }
}

