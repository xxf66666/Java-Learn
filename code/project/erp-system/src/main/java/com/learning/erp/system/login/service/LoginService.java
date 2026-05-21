package com.learning.erp.system.login.service;

import com.learning.erp.common.exception.BusinessException;
import com.learning.erp.common.exception.ErrorCode;
import com.learning.erp.framework.security.JwtUtil;
import com.learning.erp.system.user.entity.SysUser;
import com.learning.erp.system.user.service.SysUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录服务：校验密码 + 生成 JWT + 返回用户信息和权限
 *
 * 区别于 Week 8 的 AuthService：
 *  - 不再用 AuthenticationManager 全套流程（更直接）
 *  - 自己拿用户、自己 BCrypt 匹配密码
 */
@Service
public class LoginService {

    // 三个依赖
    private final SysUserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginService(SysUserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /** 登录主流程 */
    public Map<String, Object> login(String username, String password) {

        // 1) 查用户
        SysUser u = userService.findByUsername(username);
        if (u == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);

        // 2) 状态检查
        // 这里写 !=1 是因为 status 0=禁用 1=启用，可能将来还会有 2=锁定
        if (u.getStatus() != null && u.getStatus() != 1) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // 3) 密码比对
        // passwordEncoder.matches(明文, 加密后)：BCrypt 内部能从加密字符串里读出 salt 重新算
        if (!passwordEncoder.matches(password, u.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_INCORRECT);
        }

        // 4) 生成 JWT token
        String token = jwtUtil.generate(u.getId(), u.getUsername());

        // 5) 返回给前端的 JSON 内容
        Map<String, Object> m = new HashMap<>();
        m.put("token", token);
        m.put("userId", u.getId());
        m.put("username", u.getUsername());
        m.put("nickname", u.getNickname());
        // 同时返回权限列表，前端可以缓存避免每个页面都拉
        m.put("permissions", userService.loadPermissions(u.getId()));
        return m;
    }
}
