package com.learning.erp.common.util;

import java.util.Optional;

/**
 * Security 上下文工具：剥离 controller / service 的直接依赖。
 * 真实实现在 erp-framework；erp-common 只暴露一个静态门面。
 *
 * 设计目的：erp-common 不能依赖 Spring Security（在 erp-framework 才引入）
 * 所以这里用"门面 + 接口"的方式：
 *   - erp-common 提供静态门面：SecurityUtils.currentUserId()
 *   - erp-framework 在启动时把真实实现 setHolder(...) 进来
 */
public class SecurityUtils {

    // 默认实现：返回空，避免没启动 framework 时 NPE
    // static 字段：全局唯一持有者
    private static UserHolder holder = new UserHolder() {
        @Override public Optional<Long> currentUserId() { return Optional.empty(); }
        @Override public Optional<String> currentUsername() { return Optional.empty(); }
    };

    /**
     * 由 erp-framework 启动时调用，注入真实实现
     */
    public static void setHolder(UserHolder h) { holder = h; }

    /**
     * 拿当前登录用户 id（未登录返回 null）
     * .orElse(null) Optional 转 null
     */
    public static Long currentUserId() { return holder.currentUserId().orElse(null); }
    public static String currentUsername() { return holder.currentUsername().orElse(null); }

    /**
     * UserHolder 接口：定义"如何拿当前用户"
     * 实现细节交给上层模块（erp-framework）
     */
    public interface UserHolder {
        Optional<Long> currentUserId();
        Optional<String> currentUsername();
    }
}
