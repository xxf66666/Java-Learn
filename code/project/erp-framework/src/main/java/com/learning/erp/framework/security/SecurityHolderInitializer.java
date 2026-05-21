package com.learning.erp.framework.security;

import com.learning.erp.common.util.SecurityUtils;
// @PostConstruct: Bean 初始化完成后自动跑一次
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 把 Spring Security 的当前用户对接到 erp-common 的 SecurityUtils
 *
 * 这里就是"门面 + 实现"模式的实现注册点：
 *  - erp-common 定义了 SecurityUtils 静态门面
 *  - erp-framework 启动时把 Spring Security 的 SecurityContextHolder 注入进去
 *  - 业务模块 (erp-system / erp-business) 调 SecurityUtils.currentUserId() 即可
 */
@Component
public class SecurityHolderInitializer {

    /**
     * 启动后跑一次：把"如何从 Spring Security 拿当前用户"的实现塞进 SecurityUtils
     */
    @PostConstruct
    public void init() {
        // 用匿名内部类实现 UserHolder 接口
        // 也可以用 Lambda 但这个接口有 2 个方法，Lambda 表达不了
        SecurityUtils.setHolder(new SecurityUtils.UserHolder() {

            @Override
            public Optional<Long> currentUserId() {
                // 从线程绑定的 SecurityContext 拿 Authentication
                Authentication a = SecurityContextHolder.getContext().getAuthentication();

                // 模式匹配 + null 安全
                // a != null && a.getPrincipal() 是 LoginUser 类型时，u 直接可用
                if (a != null && a.getPrincipal() instanceof LoginUser u) {
                    return Optional.ofNullable(u.getUserId());
                }
                return Optional.empty();
            }

            @Override
            public Optional<String> currentUsername() {
                Authentication a = SecurityContextHolder.getContext().getAuthentication();
                if (a != null && a.getPrincipal() instanceof LoginUser u) {
                    return Optional.ofNullable(u.getUsername());
                }
                return Optional.empty();
            }
        });
    }
}
