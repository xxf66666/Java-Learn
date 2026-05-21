package com.learning.erp.system.menu.controller;

import com.learning.erp.common.result.Result;
import com.learning.erp.common.util.SecurityUtils;
import com.learning.erp.system.menu.entity.SysMenu;
import com.learning.erp.system.menu.mapper.SysMenuMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class SysMenuController {

    private final SysMenuMapper mapper;

    public SysMenuController(SysMenuMapper mapper) { this.mapper = mapper; }

    /** 当前登录用户能看到的菜单（用于前端动态渲染） */
    @GetMapping("/me")
    public Result<List<SysMenu>> myMenus() {
        Long uid = SecurityUtils.currentUserId();
        return Result.ok(mapper.selectByUserId(uid));
    }
}
