package com.learning.erp.common.util;

import java.util.Optional;

/**
 * Security 上下文工具：剥离 controller / service 的直接依赖。
 * 真实实现在 erp-framework；erp-common 只暴露一个静态门面。
 */
public class SecurityUtils {

    private static UserHolder holder = new UserHolder() {
        @Override public Optional<Long> currentUserId() { return Optional.empty(); }
        @Override public Optional<String> currentUsername() { return Optional.empty(); }
    };

    public static void setHolder(UserHolder h) { holder = h; }

    public static Long currentUserId() { return holder.currentUserId().orElse(null); }
    public static String currentUsername() { return holder.currentUsername().orElse(null); }

    public interface UserHolder {
        Optional<Long> currentUserId();
        Optional<String> currentUsername();
    }
}
