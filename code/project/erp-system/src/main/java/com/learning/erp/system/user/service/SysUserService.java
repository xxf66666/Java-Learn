package com.learning.erp.system.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.erp.common.exception.BusinessException;
import com.learning.erp.common.exception.ErrorCode;
import com.learning.erp.framework.security.LoginUser;
import com.learning.erp.framework.security.callback.LoginUserLoader;
import com.learning.erp.system.menu.entity.SysMenu;
import com.learning.erp.system.menu.mapper.SysMenuMapper;
import com.learning.erp.system.role.entity.SysRole;
import com.learning.erp.system.role.mapper.SysRoleMapper;
import com.learning.erp.system.user.entity.SysUser;
import com.learning.erp.system.user.mapper.SysUserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysUserService implements LoginUserLoader {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final PasswordEncoder passwordEncoder;

    public SysUserService(SysUserMapper userMapper,
                          SysRoleMapper roleMapper,
                          SysMenuMapper menuMapper,
                          PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public SysUser findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }

    public SysUser register(String username, String password, String nickname) {
        if (findByUsername(username) != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
        SysUser u = new SysUser();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setNickname(nickname == null ? username : nickname);
        u.setStatus(1);
        userMapper.insert(u);
        return u;
    }

    public Set<String> loadPermissions(Long userId) {
        // 用户的所有角色
        List<SysRole> roles = roleMapper.selectByUserId(userId);
        if (roles.isEmpty()) return new HashSet<>();

        // admin 直接返回 *
        if (roles.stream().anyMatch(r -> "admin".equalsIgnoreCase(r.getCode()))) {
            return Set.of("*:*:*");
        }

        // 所有角色对应的菜单权限标识
        List<SysMenu> menus = menuMapper.selectByUserId(userId);
        return menus.stream()
                .map(SysMenu::getPermission)
                .filter(p -> p != null && !p.isBlank())
                .collect(Collectors.toSet());
    }

    @Override
    public LoginUser load(Long userId, String username) {
        SysUser u = userMapper.selectById(userId);
        if (u == null || u.getStatus() != 1) return null;
        Set<String> perms = loadPermissions(userId);
        return new LoginUser(u.getId(), u.getUsername(), u.getPassword(), perms);
    }
}
