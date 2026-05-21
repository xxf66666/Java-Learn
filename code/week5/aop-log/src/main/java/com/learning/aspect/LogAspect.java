package com.learning.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    /** 切点 1：所有 com.learning.service 包下的方法 */
    @Around("execution(* com.learning.service..*.*(..))")
    public Object logService(ProceedingJoinPoint pjp) throws Throwable {
        return logAround("SERVICE", pjp);
    }

    /** 切点 2：所有标了 @LogTime 的方法 */
    @Around("@annotation(com.learning.aspect.LogTime)")
    public Object logAnnotated(ProceedingJoinPoint pjp) throws Throwable {
        return logAround("@LogTime", pjp);
    }

    private Object logAround(String tag, ProceedingJoinPoint pjp) throws Throwable {
        String m = pjp.getSignature().toShortString();
        Object[] args = pjp.getArgs();
        long t = System.currentTimeMillis();
        log.info("[{}] → {} {}", tag, m, Arrays.toString(args));
        try {
            Object result = pjp.proceed();
            log.info("[{}] ← {} {} ({} ms)", tag, m, result, System.currentTimeMillis() - t);
            return result;
        } catch (Throwable e) {
            log.error("[{}] ✗ {} {}", tag, m, e.toString());
            throw e;
        }
    }
}
