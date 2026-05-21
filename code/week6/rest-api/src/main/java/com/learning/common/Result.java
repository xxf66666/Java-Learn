package com.learning.common;

/**
 * 通用 API 响应：{ code, message, data }
 *
 * <T> 是泛型类型参数：让 data 可以是任意类型而保持类型安全
 * 调用：Result.ok(user) 返回 Result<User>，前端拿到的 JSON 是 { code: 0, ..., data: {...} }
 */
public class Result<T> {
    // code = 0 表示业务成功；非 0 表示具体错误
    private int code;
    // 人类可读的消息（前端可能用来弹 Toast）
    private String message;
    // 实际数据载体
    private T data;

    /**
     * 静态工厂方法：构造一个"成功"响应
     * static + 泛型方法：<T> 写在 static 后、返回类型前
     */
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();        // <> 菱形语法，编译器推断 T
        r.code = 0;
        r.message = "ok";
        r.data = data;
        return r;
    }

    /** 重载：无 data 的成功响应 */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /** 失败响应 */
    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }

    // Jackson 序列化时根据 getter 输出 JSON 字段
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
