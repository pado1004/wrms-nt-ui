package com.wrms.newtype.shared.common.exception;

/**
 * 비즈니스 예외의 기본 클래스
 * 모든 도메인 예외는 이 클래스를 상속받아야 합니다.
 */
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}

