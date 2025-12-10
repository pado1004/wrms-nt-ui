package com.wrms.newtype.user.api.exception;

import com.wrms.newtype.shared.common.exception.BusinessException;
import com.wrms.newtype.shared.common.constant.ErrorCode;

/**
 * 사용자를 찾을 수 없을 때 발생하는 예외
 */
public class UserNotFoundException extends BusinessException {
    
    public UserNotFoundException(Long id) {
        super(
            ErrorCode.USER_NOT_FOUND.getCode(),
            String.format("User not found with id: %d", id)
        );
    }
}

