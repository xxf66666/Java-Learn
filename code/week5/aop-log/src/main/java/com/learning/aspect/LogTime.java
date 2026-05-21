package com.learning.aspect;

import java.lang.annotation.*;

/** 自定义注解：打了它的方法会被切面拦截并打印耗时 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogTime {
}
