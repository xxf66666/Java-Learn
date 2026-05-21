package com.learning.auth.common;

public enum ErrorCode {
    SUCCESS(0, "成功"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器异常"),

    USER_NOT_FOUND(10001, "用户不存在"),
    USER_ALREADY_EXISTS(10002, "用户已存在"),
    PASSWORD_INCORRECT(10003, "密码错误"),
    ;

    private final int code;
    private final String message;
    ErrorCode(int code, String msg) { this.code = code; this.message = msg; }
    public int getCode() { return code; }
    public String getMessage() { return message; }
}
