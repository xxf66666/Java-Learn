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

@Service
public class LoginService {

    private final SysUserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginService(SysUserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> login(String username, String password) {
        SysUser u = userService.findByUsername(username);
        if (u == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        if (u.getStatus() != null && u.getStatus() != 1) throw new BusinessException(ErrorCode.USER_DISABLED);
        if (!passwordEncoder.matches(password, u.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_INCORRECT);
        }

        String token = jwtUtil.generate(u.getId(), u.getUsername());

        Map<String, Object> m = new HashMap<>();
        m.put("token", token);
        m.put("userId", u.getId());
        m.put("username", u.getUsername());
        m.put("nickname", u.getNickname());
        m.put("permissions", userService.loadPermissions(u.getId()));
        return m;
    }
}
