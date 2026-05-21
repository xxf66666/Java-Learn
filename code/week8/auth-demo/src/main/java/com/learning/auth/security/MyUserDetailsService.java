package com.learning.auth.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.auth.entity.SysUser;
import com.learning.auth.mapper.SysUserMapper;
// Spring Security 内置的 User 类（实现 UserDetails 接口）
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
// 这个接口是 Spring Security 加载用户的标准入口
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义 UserDetailsService：告诉 Spring Security "我的用户从哪查"
 *
 * 当 AuthenticationManager.authenticate() 被调用时，
 * Spring Security 会调本类的 loadUserByUsername 拿用户信息进行密码比对
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final SysUserMapper userMapper;

    public MyUserDetailsService(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 用 MyBatis-Plus 的 LambdaQueryWrapper 按用户名查
        SysUser u = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));

        // 找不到：必须抛 UsernameNotFoundException（Spring Security 约定）
        if (u == null) throw new UsernameNotFoundException("用户不存在");

        // Spring Security 提供 builder 模式构造 UserDetails
        // 密码存的是加密后的，框架会用 PasswordEncoder.matches 比对原文
        return User.builder()
                .username(u.getUsername())
                .password(u.getPassword())                  // BCrypt 加密后的密码
                .authorities("ROLE_" + u.getRole())          // 角色权限，加 ROLE_ 前缀
                .build();
    }
}
