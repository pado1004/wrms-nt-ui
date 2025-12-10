package com.wrms.newtype.customer.internal.application.service;

import com.wrms.newtype.customer.api.dto.request.CreateCustomerRequest;
import com.wrms.newtype.customer.api.dto.request.UpdateCustomerRequest;
import com.wrms.newtype.customer.api.dto.response.CustomerResponse;
import com.wrms.newtype.customer.api.exception.CustomerNotFoundException;
import com.wrms.newtype.customer.api.service.CustomerCommandService;
import com.wrms.newtype.customer.api.service.CustomerQueryService;
import com.wrms.newtype.customer.internal.domain.Customer;
import com.wrms.newtype.customer.internal.infrastructure.persistence.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 고객 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerCommandService, CustomerQueryService {
    
    private final CustomerRepository customerRepository;
    
    @Override
    public CustomerResponse create(CreateCustomerRequest request) {
        Customer customer = Customer.builder()
            .name(request.name())
            .email(request.email())
            .phone(request.phone())
            .address(request.address())
            .build();
        
        Customer saved = customerRepository.save(customer);
        return CustomerResponse.from(saved);
    }
    
    @Override
    public void update(Long id, UpdateCustomerRequest request) {
        Customer customer = findCustomerById(id);
        customer.update(request.name(), request.email(), request.phone(), request.address());
        customerRepository.save(customer);
    }
    
    @Override
    public void delete(Long id) {
        findCustomerById(id);
        customerRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        Customer customer = findCustomerById(id);
        return CustomerResponse.from(customer);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return StreamSupport.stream(customerRepository.findAll().spliterator(), false)
            .map(CustomerResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchByName(String name) {
        return customerRepository.findByNameContaining("%" + name + "%").stream()
            .map(CustomerResponse::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 고객 조회 (내부용)
     */
    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException(id));
    }
}

