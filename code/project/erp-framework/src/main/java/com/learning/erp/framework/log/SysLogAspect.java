package com.learning.erp.framework.log;

import com.learning.erp.common.annotation.SysLog;
import com.learning.erp.common.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
// 拿当前 HTTP 请求对象的工具（在切面里也能用）
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 操作日志切面
 *
 * 拦截所有标了 @SysLog 注解的方法，收集：
 *  谁(userId) / 何时(createdAt) / 做了什么(module + operation) /
 *  参数(params) / 来源(IP/UA) / 耗时 / 成败
 *
 * 通过 OperationLogSink 接口落地（具体实现在 erp-system）
 */
@Aspect
@Component
public class SysLogAspect {

    private static final Logger log = LoggerFactory.getLogger(SysLogAspect.class);

    private final OperationLogSink sink;

    public SysLogAspect(OperationLogSink sink) { this.sink = sink; }

    /**
     * @Around("@annotation(sysLog)")
     *   - 拦截标了 @SysLog 注解的方法
     *   - 第二个参数 sysLog 直接拿到注解实例（拿 module / operation 用）
     */
    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint pjp, SysLog sysLog) throws Throwable {

        // 构造日志事件对象
        OperationLogEvent ev = new OperationLogEvent();
        ev.setModule(sysLog.module());                                    // 注解里的模块名
        ev.setOperation(sysLog.operation());                               // 注解里的操作名
        ev.setMethod(pjp.getSignature().toShortString());                  // 反射拿方法签名
        ev.setParams(safeArgs(pjp.getArgs()));                              // 入参（截断防过大）

        // 通过 SecurityUtils 拿当前用户（已脱离 Spring Security 直接依赖）
        ev.setUserId(SecurityUtils.currentUserId());
        ev.setUsername(SecurityUtils.currentUsername());

        // 拿当前 HTTP 请求（可能为 null：非 Web 场景）
        HttpServletRequest req = currentRequest();
        if (req != null) {
            ev.setIp(req.getRemoteAddr());
            ev.setUserAgent(req.getHeader("User-Agent"));
        }
        ev.setCreatedAt(LocalDateTime.now());

        // 开始计时
        long t = System.currentTimeMillis();
        try {
            // 调真实方法
            Object result = pjp.proceed();
            ev.setSuccess(1);
            return result;
        } catch (Throwable e) {
            // 失败也要记录
            ev.setSuccess(0);
            ev.setErrorMsg(truncate(e.getMessage(), 1000));
            // 重抛出异常，不能吞掉
            throw e;
        } finally {
            // 无论成败都跑：算耗时 + 持久化
            ev.setDurationMs(System.currentTimeMillis() - t);
            try {
                // sink 是接口，实现异步入库（在 erp-system 的 OperationLogSinkImpl）
                sink.persist(ev);
            } catch (Exception ex) {
                // 日志入库失败不能影响业务，只打 error
                log.error("操作日志入库失败", ex);
            }
        }
    }

    /** 拿当前 HTTP 请求；不在 Web 上下文返回 null */
    private static HttpServletRequest currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        // 模式匹配：attrs 是 ServletRequestAttributes 时拿到 request
        return attrs instanceof ServletRequestAttributes sra ? sra.getRequest() : null;
    }

    /** 序列化参数并截断 */
    private static String safeArgs(Object[] args) {
        String s = Arrays.toString(args);
        return truncate(s, 2000);
    }

    /** 字符串截断 */
    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
