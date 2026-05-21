package com.learning.erp.framework.security.callback;

import com.learning.erp.framework.security.LoginUser;

/**
 * 由 erp-system 实现：根据 JWT 解析出的 userId 加载完整 LoginUser（含权限）。
 * 用 SPI 风格解耦：framework 不依赖 system。
 */
public interface LoginUserLoader {
    LoginUser load(Long userId, String username);
}
