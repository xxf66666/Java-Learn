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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class SysLogAspect {

    private static final Logger log = LoggerFactory.getLogger(SysLogAspect.class);

    private final OperationLogSink sink;

    public SysLogAspect(OperationLogSink sink) { this.sink = sink; }

    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint pjp, SysLog sysLog) throws Throwable {
        OperationLogEvent ev = new OperationLogEvent();
        ev.setModule(sysLog.module());
        ev.setOperation(sysLog.operation());
        ev.setMethod(pjp.getSignature().toShortString());
        ev.setParams(safeArgs(pjp.getArgs()));
        ev.setUserId(SecurityUtils.currentUserId());
        ev.setUsername(SecurityUtils.currentUsername());

        HttpServletRequest req = currentRequest();
        if (req != null) {
            ev.setIp(req.getRemoteAddr());
            ev.setUserAgent(req.getHeader("User-Agent"));
        }
        ev.setCreatedAt(LocalDateTime.now());

        long t = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            ev.setSuccess(1);
            return result;
        } catch (Throwable e) {
            ev.setSuccess(0);
            ev.setErrorMsg(truncate(e.getMessage(), 1000));
            throw e;
        } finally {
            ev.setDurationMs(System.currentTimeMillis() - t);
            try { sink.persist(ev); } catch (Exception ex) {
                log.error("操作日志入库失败", ex);
            }
        }
    }

    private static HttpServletRequest currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        return attrs instanceof ServletRequestAttributes sra ? sra.getRequest() : null;
    }

    private static String safeArgs(Object[] args) {
        String s = Arrays.toString(args);
        return truncate(s, 2000);
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
