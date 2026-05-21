package com.learning.auth.security;

import io.jsonwebtoken.Claims;
// Filter / Servlet 相关（Jakarta EE 9+ 改包名 jakarta.*）
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Spring Security 提供的"用户名 + 密码 token"，登录成功后塞进 SecurityContext
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// 简单权限实现
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// SecurityContextHolder 是线程隔离的"当前认证信息"持有者
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
// OncePerRequestFilter 保证一个请求只经过一次（即使有 forward）
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 鉴权过滤器：每个请求经过它，把 Authorization 头里的 token 解析成 Spring Security 的 Authentication
 *
 * @Component 让 Spring 容器管理
 * 后面 SecurityConfig 会通过 addFilterBefore 把它挂到过滤器链上
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * doFilterInternal: 每个请求实际处理逻辑
     * 必须最后调 chain.doFilter 让请求继续往后走（到下一个 Filter 或 Controller）
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse rsp, FilterChain chain)
            throws ServletException, IOException {

        // 1) 从请求头取 Authorization
        String auth = req.getHeader("Authorization");

        // 2) 必须以 "Bearer " 开头（JWT 标准约定）
        if (auth != null && auth.startsWith("Bearer ")) {
            // 取出 "Bearer " 后面的 token 字符串
            String token = auth.substring(7);

            try {
                // 3) 解析 token（签名错 / 过期会抛异常）
                Claims claims = jwtUtil.parse(token);

                // 取出我们生成时塞的字段
                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                // 4) 构造权限列表（Spring Security 要求 ROLE_ 前缀代表"角色"）
                List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

                // 5) 构造 "已认证" 的 Authentication 对象
                //   参数：principal（这里是用户名）, credentials（密码，已不需要传 null）, 权限
                UsernamePasswordAuthenticationToken token2 =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

                // 6) 塞进 SecurityContext，本请求后续就是"已认证"状态
                //    Spring Security 的 @PreAuthorize / 后续过滤器都从这里读
                SecurityContextHolder.getContext().setAuthentication(token2);
            } catch (Exception e) {
                // token 无效就不设置 Authentication
                // 后续到 Controller 时被 SecurityFilterChain 当作未认证拦截
                log.debug("JWT 校验失败: {}", e.getMessage());
            }
        }

        // 一定要继续传递，否则请求卡死
        chain.doFilter(req, rsp);
    }
}
