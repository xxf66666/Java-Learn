package com.learning.controller;

import com.learning.service.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    private final UserService service;

    public UserController(UserService service) {        // 构造器注入
        this.service = service;
    }

    public String register(String name) {
        Long id = service.register(name);
        return "已注册 id=" + id;
    }

    public String greet(long id) {
        return service.greet(id);
    }
}
