package com.wrms.nt.domain.customer.service;

import com.wrms.nt.domain.customer.entity.Customer;
import com.wrms.nt.domain.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 고객 서비스
 */
@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * 모든 고객 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAllOrderByCreatedAtDesc();
    }

    /**
     * 고객 ID로 조회
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    /**
     * 이메일로 고객 조회
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    /**
     * 전화번호로 고객 조회
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findByPhone(String phone) {
        return customerRepository.findByPhone(phone);
    }

    /**
     * 고객명으로 검색
     */
    @Transactional(readOnly = true)
    public List<Customer> searchByName(String name) {
        return customerRepository.findByNameContaining("%" + name + "%");
    }

    /**
     * 고객 저장 (등록/수정)
     */
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            customer.setCreatedAt(LocalDateTime.now());
        }
        customer.setUpdatedAt(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    /**
     * 고객 삭제
     */
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }

}
