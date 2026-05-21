package com.learning.erp.system.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.erp.common.exception.BusinessException;
import com.learning.erp.common.exception.ErrorCode;
import com.learning.erp.framework.security.LoginUser;
// 实现 framework 提供的 SPI 接口
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

/**
 * 用户服务（系统层）
 *
 * 同时实现 LoginUserLoader 接口：让 framework 的 JwtAuthFilter 能调用这里加载完整用户
 */
@Service
public class SysUserService implements LoginUserLoader {

    // 三个依赖：用户/角色/菜单 Mapper + 密码加密器
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final PasswordEncoder passwordEncoder;

    // 构造器注入
    public SysUserService(SysUserMapper userMapper,
                          SysRoleMapper roleMapper,
                          SysMenuMapper menuMapper,
                          PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /** 按用户名查 */
    public SysUser findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }

    /** 注册（密码加密 + 校验唯一） */
    public SysUser register(String username, String password, String nickname) {
        // 唯一性校验
        if (findByUsername(username) != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        SysUser u = new SysUser();
        u.setUsername(username);
        // 密码 BCrypt 加密后存
        u.setPassword(passwordEncoder.encode(password));
        // 三元 + null 检查
        u.setNickname(nickname == null ? username : nickname);
        u.setStatus(1);
        userMapper.insert(u);
        return u;
    }

    /**
     * 加载用户的所有权限标识
     * RBAC 流程：user → roles → menus → permissions
     */
    public Set<String> loadPermissions(Long userId) {
        // 用户的所有角色（通过 sys_user_role 中间表 join）
        List<SysRole> roles = roleMapper.selectByUserId(userId);
        if (roles.isEmpty()) return new HashSet<>();

        // admin 角色直接给"全权限"通配符 *:*:*
        // anyMatch + 方法引用 + equalsIgnoreCase 忽略大小写
        if (roles.stream().anyMatch(r -> "admin".equalsIgnoreCase(r.getCode()))) {
            return Set.of("*:*:*");
        }

        // 普通用户：查所有菜单的 permission 字段
        List<SysMenu> menus = menuMapper.selectByUserId(userId);
        return menus.stream()
                .map(SysMenu::getPermission)                    // 取 permission 字段
                .filter(p -> p != null && !p.isBlank())          // 过滤空值
                .collect(Collectors.toSet());                     // 收集成 Set
    }

    /**
     * 实现 LoginUserLoader 接口
     * 由 framework 的 JwtAuthFilter 调用，每次请求都加载完整 LoginUser
     */
    @Override
    public LoginUser load(Long userId, String username) {
        SysUser u = userMapper.selectById(userId);
        // 用户不存在或被禁用 → 视为未登录
        if (u == null || u.getStatus() != 1) return null;

        // 实时查权限（不缓存）：权限改了立即生效
        Set<String> perms = loadPermissions(userId);

        return new LoginUser(u.getId(), u.getUsername(), u.getPassword(), perms);
    }
}
