package com.learning.erp.common.annotation;

import java.lang.annotation.*;

/** 标注后由 SysLogAspect 自动入库操作日志 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    String module() default "";
    String operation() default "";
}
