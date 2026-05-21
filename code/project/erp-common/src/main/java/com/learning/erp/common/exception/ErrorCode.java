package com.learning.erp.common.exception;

public enum ErrorCode {
    SUCCESS(0, "成功"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器异常"),

    // 用户 1xxxx
    USER_NOT_FOUND(10001, "用户不存在"),
    USER_ALREADY_EXISTS(10002, "用户已存在"),
    PASSWORD_INCORRECT(10003, "密码错误"),
    USER_DISABLED(10004, "用户已被禁用"),

    // 角色 / 菜单 2xxxx
    ROLE_NOT_FOUND(20001, "角色不存在"),
    MENU_NOT_FOUND(20002, "菜单不存在"),

    // 物料 3xxxx
    MATERIAL_NOT_FOUND(30001, "物料不存在"),

    // 库存 4xxxx
    STOCK_INSUFFICIENT(40001, "库存不足"),

    // 采购 / 销售 5xxxx / 6xxxx
    PURCHASE_ORDER_NOT_FOUND(50001, "采购单不存在"),
    SALE_ORDER_NOT_FOUND(60001, "销售单不存在"),
    ;

    private final int code;
    private final String message;
    ErrorCode(int code, String message) {
        this.code = code; this.message = message;
    }
    public int getCode() { return code; }
    public String getMessage() { return message; }
}
