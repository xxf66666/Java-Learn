package com.learning.erp.system.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.erp.common.annotation.SysLog;
import com.learning.erp.common.exception.BusinessException;
import com.learning.erp.common.exception.ErrorCode;
import com.learning.erp.common.result.PageResult;
import com.learning.erp.common.result.Result;
import com.learning.erp.system.user.entity.SysUser;
import com.learning.erp.system.user.mapper.SysUserMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class SysUserController {

    private final SysUserMapper userMapper;

    public SysUserController(SysUserMapper userMapper) { this.userMapper = userMapper; }

    @PreAuthorize("hasAnyAuthority('*:*:*', 'sys:user:list')")
    @GetMapping
    public Result<PageResult<SysUser>> list(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String username) {
        var wrapper = new LambdaQueryWrapper<SysUser>()
            .like(StringUtils.hasText(username), SysUser::getUsername, username)
            .orderByDesc(SysUser::getId);
        Page<SysUser> p = userMapper.selectPage(new Page<>(page, size), wrapper);
        // 去掉密码
        p.getRecords().forEach(u -> u.setPassword(null));
        return Result.ok(new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    @PreAuthorize("hasAnyAuthority('*:*:*', 'sys:user:query')")
    @GetMapping("/{id}")
    public Result<SysUser> get(@PathVariable Long id) {
        SysUser u = userMapper.selectById(id);
        if (u == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        u.setPassword(null);
        return Result.ok(u);
    }

    @PreAuthorize("hasAnyAuthority('*:*:*', 'sys:user:edit')")
    @SysLog(module = "用户", operation = "修改")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser u) {
        u.setId(id);
        u.setPassword(null);
        userMapper.updateById(u);
        return Result.ok();
    }

    @PreAuthorize("hasAnyAuthority('*:*:*', 'sys:user:remove')")
    @SysLog(module = "用户", operation = "删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.ok();
    }
}
