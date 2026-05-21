package com.learning.erp.framework.security;

import com.learning.erp.common.util.SecurityUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** 把 Spring Security 的当前用户对接到 erp-common 的 SecurityUtils */
@Component
public class SecurityHolderInitializer {

    @PostConstruct
    public void init() {
        SecurityUtils.setHolder(new SecurityUtils.UserHolder() {
            @Override
            public Optional<Long> currentUserId() {
                Authentication a = SecurityContextHolder.getContext().getAuthentication();
                if (a != null && a.getPrincipal() instanceof LoginUser u) {
                    return Optional.ofNullable(u.getUserId());
                }
                return Optional.empty();
            }
            @Override
            public Optional<String> currentUsername() {
                Authentication a = SecurityContextHolder.getContext().getAuthentication();
                if (a != null && a.getPrincipal() instanceof LoginUser u) {
                    return Optional.ofNullable(u.getUsername());
                }
                return Optional.empty();
            }
        });
    }
}
