package com.learning.auth.common;

public class Result<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp = System.currentTimeMillis();

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = 0; r.message = "ok"; r.data = data;
        return r;
    }
    public static <T> Result<T> ok() { return ok(null); }
    public static <T> Result<T> fail(int code, String msg) {
        Result<T> r = new Result<>();
        r.code = code; r.message = msg;
        return r;
    }
    public static <T> Result<T> fail(ErrorCode e) {
        return fail(e.getCode(), e.getMessage());
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public long getTimestamp() { return timestamp; }
}
