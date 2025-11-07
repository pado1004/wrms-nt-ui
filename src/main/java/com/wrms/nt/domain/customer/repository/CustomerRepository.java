package com.wrms.nt.domain.customer.repository;

import com.wrms.nt.domain.customer.entity.Customer;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 고객 리포지토리
 */
@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    /**
     * 모든 고객 목록 조회 (최신순)
     */
    @Query("SELECT * FROM customer ORDER BY created_at DESC")
    List<Customer> findAllOrderByCreatedAtDesc();

    /**
     * 이메일로 고객 조회
     */
    @Query("SELECT * FROM customer WHERE email = :email")
    Optional<Customer> findByEmail(@Param("email") String email);

    /**
     * 전화번호로 고객 조회
     */
    @Query("SELECT * FROM customer WHERE phone = :phone")
    Optional<Customer> findByPhone(@Param("phone") String phone);

    /**
     * 고객명으로 검색
     */
    @Query("SELECT * FROM customer WHERE name LIKE :name ORDER BY created_at DESC")
    List<Customer> findByNameContaining(@Param("name") String name);

}
