package com.learning.erp.framework.security;

// SPI 风格回调：framework 不能依赖 system，所以定义接口让 system 实现
import com.learning.erp.framework.security.callback.LoginUserLoader;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ERP 的 JWT 鉴权过滤器
 *
 * 和 Week 8 简化版的差异：
 *  - 不在 token 里存 role / permissions（避免登录后改权限不生效）
 *  - 每个请求都通过 LoginUserLoader 实时查最新权限
 *  - LoginUserLoader 由 erp-system 实现，framework 不直接依赖 system
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;
    private final LoginUserLoader loader;

    // 构造器注入两个依赖
    public JwtAuthFilter(JwtUtil jwtUtil, LoginUserLoader loader) {
        this.jwtUtil = jwtUtil;
        this.loader = loader;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse rsp, FilterChain chain)
            throws ServletException, IOException {

        // 1) 从 Authorization 头取 Bearer token
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // 2) 解析 JWT
                Claims claims = jwtUtil.parse(token);
                Long userId = claims.get("userId", Long.class);
                String username = claims.getSubject();

                // 3) 调 system 模块的 SysUserService 加载完整用户信息（含权限）
                //    每个请求都查一次，权限改动立即生效；如果担心性能可以加 Redis 缓存
                LoginUser loginUser = loader.load(userId, username);

                if (loginUser != null) {
                    // 4) 构造已认证的 Authentication
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                            loginUser,                         // principal: 整个 LoginUser
                            null,                              // credentials: 密码（不传）
                            loginUser.getAuthorities());        // 权限列表

                    // 5) 放进 SecurityContext，本请求后续就是"已认证"
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                // token 失效或用户被删 → 视为未登录
                log.debug("JWT 校验失败: {}", e.getMessage());
            }
        }

        // 必须继续传递请求，不然请求卡死
        chain.doFilter(req, rsp);
    }
}
