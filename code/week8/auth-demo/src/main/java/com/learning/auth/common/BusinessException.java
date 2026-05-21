package com.learning.auth.common;

public class BusinessException extends RuntimeException {
    private final int code;
    public BusinessException(int code, String msg) { super(msg); this.code = code; }
    public BusinessException(ErrorCode e) { super(e.getMessage()); this.code = e.getCode(); }
    public int getCode() { return code; }
}
