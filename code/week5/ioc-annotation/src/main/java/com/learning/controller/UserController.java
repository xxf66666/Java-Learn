package com.learning.controller;

import com.learning.service.UserService;
// @Controller 是 @Component 别名，标记"Web 控制层"
// 纯 Spring 项目里它和 @Service 区别只是语义，行为一样
// Spring MVC 项目里 @Controller 还会扫描里面的 @RequestMapping 等
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    // final 字段 + 构造器注入：依赖关系一目了然，且不可变
    private final UserService service;

    // 构造器注入：Spring 自动把容器里的 UserService 传进来
    // 只有一个构造器时 @Autowired 可省略
    public UserController(UserService service) {
        this.service = service;
    }

    /** 注册：把请求委托给 service */
    public String register(String name) {
        Long id = service.register(name);
        return "已注册 id=" + id;
    }

    /** 打招呼 */
    public String greet(long id) {
        return service.greet(id);
    }
}
