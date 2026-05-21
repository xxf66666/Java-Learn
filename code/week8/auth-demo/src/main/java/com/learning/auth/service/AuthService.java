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

@Service
public class AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthService(SysUserMapper userMapper,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authManager,
                       JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    public Long register(String username, String password, String role) {
        SysUser exist = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (exist != null) throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);

        SysUser u = new SysUser();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setRole(role == null ? "USER" : role);
        userMapper.insert(u);
        return u.getId();
    }

    public Map<String, Object> login(String username, String password) {
        try {
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PASSWORD_INCORRECT);
        }

        SysUser u = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        String token = jwtUtil.generate(u.getUsername(), u.getId(), u.getRole());

        Map<String, Object> m = new HashMap<>();
        m.put("token", token);
        m.put("username", u.getUsername());
        m.put("role", u.getRole());
        return m;
    }
}
