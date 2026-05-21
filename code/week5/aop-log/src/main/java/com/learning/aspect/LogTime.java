package com.learning.aspect;

import java.lang.annotation.*;

/**
 * 自定义注解：打了它的方法会被切面拦截并打印耗时
 *
 * 元注解（注解的注解）：
 *   @Target  这个注解能用在什么地方
 *   @Retention 注解保留到什么时期
 */
@Target(ElementType.METHOD)        // 只能用在方法上（其它选项：TYPE 类, FIELD 字段...）
@Retention(RetentionPolicy.RUNTIME) // 运行时仍然保留，反射能读到（CLASS = 编译后丢弃；SOURCE = 源码后丢弃）
public @interface LogTime {
    // @interface 不是 interface！这是注解定义的特殊语法
    // 这个注解没有属性，所以方法体是空的
    // 如果需要属性可以加：String value() default "";
}
