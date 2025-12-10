package com.wrms.newtype.customer.api.exception;

import com.wrms.newtype.shared.common.exception.BusinessException;
import com.wrms.newtype.shared.common.constant.ErrorCode;

/**
 * 고객을 찾을 수 없을 때 발생하는 예외
 */
public class CustomerNotFoundException extends BusinessException {
    
    public CustomerNotFoundException(Long id) {
        super(
            ErrorCode.CUSTOMER_NOT_FOUND.getCode(),
            String.format("Customer not found with id: %d", id)
        );
    }
}

