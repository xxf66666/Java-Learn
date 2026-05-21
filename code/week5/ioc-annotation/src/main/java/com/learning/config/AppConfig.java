package com.learning.config;

// Spring 三个核心配置注解
// @Configuration: 标记这个类是配置类
// @ComponentScan: 指定扫描哪些包下的 @Component / @Service / @Repository / @Controller
// @PropertySource: 加载额外的 properties 文件
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Spring 配置类（纯 Java 配置，替代老的 XML）
 *
 * 标了 @Configuration 后，整个类就是 Spring 的"组装手册"。
 */
@Configuration
@ComponentScan("com.learning")                            // 从 com.learning 包开始扫描所有 @Component 系列注解
@PropertySource("classpath:app.properties")                // 加载 resources/app.properties
public class AppConfig {
    // 这个类可以是空的：仅依赖类上的注解告诉 Spring "做什么"
    // 后面学到 @Bean 时这里会加方法显式声明第三方 Bean
}
