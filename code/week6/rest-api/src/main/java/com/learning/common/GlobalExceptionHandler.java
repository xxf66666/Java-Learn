package com.learning.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// 校验失败时 Spring 抛这个
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
// @RestControllerAdvice = @ControllerAdvice + @ResponseBody
// 拦截所有 Controller 抛出的异常，并以 JSON 形式返回
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器：把异常转成统一的 Result JSON 响应
 *
 * Spring 会扫描这个类，遇到任何 Controller 抛异常时
 * 自动找匹配的 @ExceptionHandler 来处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常：把 code / message 透传给前端
     * @ExceptionHandler 指定处理的异常类型
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBiz(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理 @Valid 校验失败
     * MethodArgumentNotValidException 含所有未通过的字段错误
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        // BindingResult 装着所有错误；这里取第一个字段错的描述
        var fe = e.getBindingResult().getFieldError();
        String msg = fe != null ? fe.getDefaultMessage() : "参数校验失败";
        return Result.fail(400, msg);
    }

    /**
     * 处理 IllegalArgumentException（一般来自业务校验）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArg(IllegalArgumentException e) {
        return Result.fail(400, e.getMessage());
    }

    /**
     * 兜底：其它一切未处理的异常
     * 第二个参数 e 让 SLF4J 自动打印完整堆栈
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception e) {
        log.error("未捕获异常", e);
        return Result.fail(500, "服务器异常");
    }
}
