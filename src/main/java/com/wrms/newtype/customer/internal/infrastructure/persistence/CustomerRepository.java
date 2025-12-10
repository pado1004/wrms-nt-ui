package com.wrms.newtype.customer.internal.infrastructure.persistence;

import com.wrms.newtype.customer.internal.domain.Customer;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 고객 리포지토리
 */
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    
    /**
     * 이메일로 조회
     */
    @Query("SELECT * FROM customer WHERE email = :email")
    Optional<Customer> findByEmail(@Param("email") String email);
    
    /**
     * 전화번호로 조회
     */
    @Query("SELECT * FROM customer WHERE phone = :phone")
    Optional<Customer> findByPhone(@Param("phone") String phone);
    
    /**
     * 이름으로 검색
     */
    @Query("SELECT * FROM customer WHERE name LIKE :name ORDER BY created_at DESC")
    List<Customer> findByNameContaining(@Param("name") String name);
}

