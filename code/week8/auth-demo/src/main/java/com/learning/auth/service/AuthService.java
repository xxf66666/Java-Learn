package com.learning.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.auth.common.BusinessException;
import com.learning.auth.common.ErrorCode;
import com.learning.auth.entity.SysUser;
import com.learning.auth.mapper.SysUserMapper;
import com.learning.auth.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证业务：注册 / 登录
 */
@Service
public class AuthService {

    // 四个依赖：用户 Mapper、密码加密器、认证管理器、JWT 工具
    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    // 构造器注入（4 个依赖一行写不下，正常多行）
    public AuthService(SysUserMapper userMapper,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authManager,
                       JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    /** 注册：先查重，再 BCrypt 加密密码后保存 */
    public Long register(String username, String password, String role) {
        // 查是否已存在
        SysUser exist = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (exist != null) throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);

        SysUser u = new SysUser();
        u.setUsername(username);
        // ⭐ 密码必须加密存：BCrypt 单向哈希
        // 同一个明文每次 encode 结果不同，但 matches() 能验证正确
        u.setPassword(passwordEncoder.encode(password));
        // null 时给个默认角色
        u.setRole(role == null ? "USER" : role);
        userMapper.insert(u);
        return u.getId();
    }

    /** 登录：校验密码 + 生成 JWT */
    public Map<String, Object> login(String username, String password) {
        try {
            // 让 Spring Security 走完整认证流程：
            //   1) UserDetailsService.loadUserByUsername 查用户
            //   2) PasswordEncoder.matches 比对密码
            // 任何步骤失败抛 AuthenticationException
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            // 用户不存在 / 密码错都归到"密码错"，避免暴露用户名
            throw new BusinessException(ErrorCode.PASSWORD_INCORRECT);
        }

        // 密码对了：查出完整 user 信息用于生成 JWT
        SysUser u = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));

        // 生成 token
        String token = jwtUtil.generate(u.getUsername(), u.getId(), u.getRole());

        // 返回给前端的 JSON 结构
        Map<String, Object> m = new HashMap<>();
        m.put("token", token);
        m.put("username", u.getUsername());
        m.put("role", u.getRole());
        return m;
    }
}
