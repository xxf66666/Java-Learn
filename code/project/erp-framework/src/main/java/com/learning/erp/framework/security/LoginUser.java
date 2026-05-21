package com.learning.erp.framework.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** 装到 SecurityContext 里的"登录用户" */
public class LoginUser implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final Set<String> permissions;

    public LoginUser(Long userId, String username, String password, Set<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.permissions = permissions;
    }

    public Long getUserId() { return userId; }
    public Set<String> getPermissions() { return permissions; }

    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return password; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
