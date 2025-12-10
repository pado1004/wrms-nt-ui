package com.wrms.newtype.counseling.api.exception;

import com.wrms.newtype.shared.common.exception.BusinessException;
import com.wrms.newtype.shared.common.constant.ErrorCode;

/**
 * 상담을 찾을 수 없을 때 발생하는 예외
 */
public class CounselingNotFoundException extends BusinessException {
    
    public CounselingNotFoundException(Long id) {
        super(
            ErrorCode.COUNSELING_NOT_FOUND.getCode(),
            String.format("Counseling not found with id: %d", id)
        );
    }
}

