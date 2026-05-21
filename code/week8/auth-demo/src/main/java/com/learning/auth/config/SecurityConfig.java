package com.learning.auth.config;

import com.learning.auth.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// @EnableMethodSecurity: 开启方法级权限注解（@PreAuthorize 等）
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// @EnableWebSecurity: 开启 Spring Security Web 模块（Spring Boot 自动配会做这步，显式写更清晰）
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// SessionCreationPolicy: 会话策略
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// Spring Security 默认登录过滤器，我们要在它前面插入 JWT 过滤器
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 *
 * 三个核心 Bean：
 *  1. SecurityFilterChain 过滤器链
 *  2. PasswordEncoder 密码加密器
 *  3. AuthenticationManager 认证管理器
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity            // 开启 @PreAuthorize
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;

    public SecurityConfig(JwtAuthFilter jwtFilter) { this.jwtFilter = jwtFilter; }

    /**
     * 配置过滤器链：每个请求经过的过滤器顺序
     * HttpSecurity 是构造器风格的 DSL，链式调用配置各项
     */
    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http
            // 关 CSRF：前后端分离 + JWT 场景不需要 CSRF token
            .csrf(c -> c.disable())

            // 无状态会话：不创建 HttpSession，全靠 JWT 鉴权
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 路径授权规则：哪些路径放行 / 哪些要登录
            .authorizeHttpRequests(auth -> auth
                // permitAll: 不要求认证，所有人都能访问
                .requestMatchers("/api/login", "/api/register",
                                 "/doc.html", "/webjars/**", "/v3/api-docs/**",
                                 "/swagger-ui/**", "/swagger-resources/**").permitAll()
                // 其余所有请求都要先登录
                .anyRequest().authenticated())

            // 把 JWT 过滤器放在 UsernamePasswordAuthenticationFilter 之前
            // 这样可以先用 JWT 完成认证，跳过 form 登录
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 密码加密器：用 BCrypt
     *   - 单向加密（不能逆向，只能比对）
     *   - 自带 salt，每次加密结果不同
     *   - 慢，抗暴力破解
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器：登录时用它校验用户名密码
     * 这里从 Spring Security 内部配置里"借"一个出来当 Bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
