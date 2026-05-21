package com.learning.auth.controller;

import com.learning.auth.common.Result;
import com.learning.auth.dto.LoginReq;
import com.learning.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "认证")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) { this.service = service; }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Long> register(@RequestBody @Valid LoginReq req) {
        return Result.ok(service.register(req.getUsername(), req.getPassword(), "USER"));
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Valid LoginReq req) {
        return Result.ok(service.login(req.getUsername(), req.getPassword()));
    }

    @Operation(summary = "查看当前用户")
    @GetMapping("/me")
    public Result<Map<String, Object>> me(Authentication auth) {
        return Result.ok(Map.of(
            "username", auth.getName(),
            "authorities", auth.getAuthorities().toString()
        ));
    }

    @Operation(summary = "管理员才能调")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/secret")
    public Result<String> adminOnly() {
        return Result.ok("欢迎管理员！");
    }
}
