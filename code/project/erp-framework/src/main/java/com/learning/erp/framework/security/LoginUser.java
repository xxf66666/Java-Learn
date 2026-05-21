package com.learning.erp.framework.security;

// Spring Security 的权限接口和实现
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// UserDetails 是 Spring Security 表示"已认证用户"的接口
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 装到 SecurityContext 里的"登录用户"
 *
 * 实现 UserDetails 接口：Spring Security 整个生态都用它表示已登录用户
 * 自定义的扩展：增加了 userId 和 permissions
 */
public class LoginUser implements UserDetails {

    // final 字段：构造完锁死
    private final Long userId;
    private final String username;
    private final String password;
    // Set<String> 而不是 List：去重；查询是否有权限时 contains 是 O(1)
    private final Set<String> permissions;

    public LoginUser(Long userId, String username, String password, Set<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.permissions = permissions;
    }

    // 自定义 getter
    public Long getUserId() { return userId; }
    public Set<String> getPermissions() { return permissions; }

    // ====== UserDetails 接口要求实现的方法 ======

    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return password; }

    // 这四个状态字段都返回 true（表示账号正常）
    // 真实业务里可以根据数据库 status 字段返回
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    /**
     * 把权限字符串转成 Spring Security 的 GrantedAuthority 集合
     * 这是 @PreAuthorize 的判断依据
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)        // 方法引用 = 构造器引用，每个权限字符串包装成 SimpleGrantedAuthority
                .collect(Collectors.toList());
    }
}
