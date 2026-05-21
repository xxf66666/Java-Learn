package com.learning.common;

/**
 * 业务异常：所有"可预期的"业务错误（用户不存在、参数错等）都抛这个
 *
 * 继承 RuntimeException = unchecked，方法签名不用 throws，调用方不用 try-catch
 * @Transactional 默认能捕获到 RuntimeException 并回滚事务
 */
public class BusinessException extends RuntimeException {

    // 业务错误码（如 40401 用户不存在）
    private final int code;

    public BusinessException(int code, String message) {
        // 调父类构造器，把 message 传上去（getMessage() 用）
        super(message);
        this.code = code;
    }

    public int getCode() { return code; }
}
