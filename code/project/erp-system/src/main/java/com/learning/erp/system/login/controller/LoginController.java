package com.learning.erp.system.login.controller;

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

    public record LoginReq(@NotBlank String username, @NotBlank String password) {}
    public record RegisterReq(@NotBlank String username, @NotBlank String password, String nickname) {}

    @PostMapping("/login")
    @SysLog(module = "登录", operation = "登录")
    public Result<Map<String, Object>> login(@RequestBody LoginReq req) {
        return Result.ok(loginService.login(req.username(), req.password()));
    }

    @PostMapping("/register")
    @SysLog(module = "登录", operation = "注册")
    public Result<Long> register(@RequestBody RegisterReq req) {
        return Result.ok(userService.register(req.username(), req.password(), req.nickname()).getId());
    }

    @GetMapping("/me")
    public Result<Map<String, Object>> me() {
        return Result.ok(Map.of(
            "userId", SecurityUtils.currentUserId(),
            "username", SecurityUtils.currentUsername(),
            "permissions", userService.loadPermissions(SecurityUtils.currentUserId())
        ));
    }
}
