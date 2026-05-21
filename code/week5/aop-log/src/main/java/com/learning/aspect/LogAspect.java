package com.learning.aspect;

// AspectJ 是切面的具体实现库（Spring AOP 在底层调用它）
import org.aspectj.lang.ProceedingJoinPoint;
// @Around / @Aspect 等 AspectJ 注解
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// @Component 让 Spring 把这个切面交给容器管理
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 日志切面：用 AOP 自动给方法加"打日志"功能，业务代码无需改动。
 *
 * @Aspect 标记这是一个切面类
 * @Component 让 Spring 容器扫描到（一定要加！否则切面不生效）
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 切点 1：拦截所有 com.learning.service 包下的方法
     *
     * @Around 是"环绕通知"——方法执行**前**+**后**都能插代码
     * "execution(...)" 是 AspectJ 切点表达式：
     *   * com.learning.service..*.*(..)
     *   ↑                       ↑   ↑
     *   返回类型任意              类  方法.参数任意
     *   ..  = 包含所有子包
     */
    @Around("execution(* com.learning.service..*.*(..))")
    public Object logService(ProceedingJoinPoint pjp) throws Throwable {
        return logAround("SERVICE", pjp);
    }

    /**
     * 切点 2：拦截所有标了 @LogTime 注解的方法
     * @annotation 是另一种切点：匹配带特定注解的方法
     */
    @Around("@annotation(com.learning.aspect.LogTime)")
    public Object logAnnotated(ProceedingJoinPoint pjp) throws Throwable {
        return logAround("@LogTime", pjp);
    }

    /**
     * 公共的"环绕"逻辑：打日志 + 调真实方法 + 测耗时
     * ProceedingJoinPoint 是 AOP 提供的"被拦截方法"的句柄
     */
    private Object logAround(String tag, ProceedingJoinPoint pjp) throws Throwable {
        // 方法签名（包名.类.方法）
        String m = pjp.getSignature().toShortString();
        // 入参数组
        Object[] args = pjp.getArgs();
        // 开始计时
        long t = System.currentTimeMillis();

        // 打"进入方法"日志
        log.info("[{}] → {} {}", tag, m, Arrays.toString(args));

        try {
            // ⭐ proceed() 调用真实方法（不调用就拦截了，真实方法不执行）
            // 返回值是真实方法的返回值
            Object result = pjp.proceed();

            // 打"返回"日志，含耗时
            log.info("[{}] ← {} {} ({} ms)", tag, m, result, System.currentTimeMillis() - t);
            return result;
        } catch (Throwable e) {
            // 方法抛异常时打错误日志，然后重抛保留原异常
            log.error("[{}] ✗ {} {}", tag, m, e.toString());
            throw e;
        }
    }
}
