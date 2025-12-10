package com.wrms.newtype.shared.common.constant;

/**
 * 에러 코드 상수
 */
public enum ErrorCode {
    // Counseling
    COUNSELING_NOT_FOUND("COUNSELING_NOT_FOUND"),
    COUNSELING_VALIDATION_ERROR("COUNSELING_VALIDATION_ERROR"),
    
    // Customer
    CUSTOMER_NOT_FOUND("CUSTOMER_NOT_FOUND"),
    CUSTOMER_VALIDATION_ERROR("CUSTOMER_VALIDATION_ERROR"),
    
    // User
    USER_NOT_FOUND("USER_NOT_FOUND"),
    USER_VALIDATION_ERROR("USER_VALIDATION_ERROR"),
    
    // Notification
    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND");
    
    private final String code;
    
    ErrorCode(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}

