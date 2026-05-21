package com.learning.auth.controller;

import com.learning.auth.common.Result;
import com.learning.auth.dto.LoginReq;
import com.learning.auth.service.AuthService;
// Knife4j / SpringDoc OpenAPI 3 注解
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
// @PreAuthorize 方法级权限注解
import org.springframework.security.access.prepost.PreAuthorize;
// Spring Security 自动注入当前认证信息
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "认证")                  // Knife4j 文档分组名
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) { this.service = service; }

    /** 注册 */
    @Operation(summary = "注册")         // 文档里这个接口的标题
    @PostMapping("/register")
    public Result<Long> register(@RequestBody @Valid LoginReq req) {
        // 默认角色 USER
        return Result.ok(service.register(req.getUsername(), req.getPassword(), "USER"));
    }

    /** 登录 */
    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Valid LoginReq req) {
        return Result.ok(service.login(req.getUsername(), req.getPassword()));
    }

    /**
     * 看自己信息（要登录）
     * Authentication 参数：Spring Security 自动从 SecurityContext 注入
     */
    @Operation(summary = "查看当前用户")
    @GetMapping("/me")
    public Result<Map<String, Object>> me(Authentication auth) {
        return Result.ok(Map.of(
            "username", auth.getName(),
            "authorities", auth.getAuthorities().toString()
        ));
    }

    /**
     * 管理员才能调
     * @PreAuthorize 表达式：当前用户必须有 ROLE_ADMIN 角色
     * hasRole('ADMIN') 自动补上 ROLE_ 前缀 → 实际比对 ROLE_ADMIN
     * 没权限 Spring Security 抛 AccessDeniedException → GlobalExceptionHandler 转成 403
     */
    @Operation(summary = "管理员才能调")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/secret")
    public Result<String> adminOnly() {
        return Result.ok("欢迎管理员！");
    }
}
