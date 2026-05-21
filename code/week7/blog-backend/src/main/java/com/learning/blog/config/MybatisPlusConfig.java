package com.learning.blog.config;

// 数据库类型枚举（MySQL / Oracle / PostgreSQL...）
import com.baomidou.mybatisplus.annotation.DbType;
// MyBatis-Plus 的总拦截器，所有内部拦截器挂它上面
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
// 乐观锁拦截器（实现 @Version）
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
// 分页拦截器（让 selectPage 真正生效）
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件配置
 *
 * @Configuration 让 Spring 把这个类作为配置类
 * @Bean 方法返回的对象会被注册成 Spring Bean
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor i = new MybatisPlusInterceptor();

        // 分页插件 - 没有它 selectPage 不会真正限制结果数量
        // DbType.MYSQL 让分页 SQL 用 LIMIT 语法（不同数据库不一样）
        i.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        // 乐观锁插件 - 实体上 @Version 字段才会被 update 时自动处理
        i.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return i;
    }
}
