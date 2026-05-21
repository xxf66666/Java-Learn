package com.learning.erp.system.login.controller;

// 自动写入 sys_operation_log 的注解
import com.learning.erp.common.annotation.SysLog;
import com.learning.erp.common.result.Result;
import com.learning.erp.common.util.SecurityUtils;
import com.learning.erp.system.login.service.LoginService;
import com.learning.erp.system.user.service.SysUserService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final LoginService loginService;
    private final SysUserService userService;

    public LoginController(LoginService loginService, SysUserService userService) {
        this.loginService = loginService;
        this.userService = userService;
    }

    /**
     * record 是 Java 16+ 的紧凑数据类
     * 这里用 record 定义请求 DTO，比传统的 class + getter/setter 简洁
     * 自动生成：构造器、字段、getter（叫 username() 不是 getUsername()）、equals、hashCode、toString
     */
    public record LoginReq(@NotBlank String username, @NotBlank String password) {}
    public record RegisterReq(@NotBlank String username, @NotBlank String password, String nickname) {}

    /** 登录：标了 @SysLog → 自动记录操作日志 */
    @PostMapping("/login")
    @SysLog(module = "登录", operation = "登录")
    public Result<Map<String, Object>> login(@RequestBody LoginReq req) {
        // record 的字段访问是同名方法 username()，不是 getUsername()
        return Result.ok(loginService.login(req.username(), req.password()));
    }

    @PostMapping("/register")
    @SysLog(module = "登录", operation = "注册")
    public Result<Long> register(@RequestBody RegisterReq req) {
        return Result.ok(userService.register(req.username(), req.password(), req.nickname()).getId());
    }

    /**
     * 看我的信息 + 权限
     * 通过 SecurityUtils 拿到当前登录用户 id
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> me() {
        // Map.of(key, value, ...) 创建小 Map（最多 10 对）
        return Result.ok(Map.of(
            "userId", SecurityUtils.currentUserId(),
            "username", SecurityUtils.currentUsername(),
            "permissions", userService.loadPermissions(SecurityUtils.currentUserId())
        ));
    }
}
