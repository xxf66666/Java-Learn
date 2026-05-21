package com.learning.erp.common.exception;

public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode e) {
        super(e.getMessage());
        this.code = e.getCode();
    }

    public BusinessException(ErrorCode e, String detail) {
        super(e.getMessage() + ": " + detail);
        this.code = e.getCode();
    }

    public int getCode() { return code; }
}
