package com.learning;

import org.springframework.boot.SpringApplication;
// @SpringBootApplication 是 Spring Boot 的"启动注解"
// 它实际等价于：@SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan
import org.springframework.boot.autoconfigure.SpringBootApplication;
// MVC 相关注解
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        // SpringApplication.run 启动 Spring Boot
        // 它会做的事：
        //   1. 创建 Spring 容器
        //   2. 根据 classpath 自动装配（看你引了哪些 starter）
        //   3. 启动内嵌 Tomcat（因为引了 spring-boot-starter-web）
        //   4. 扫描 @Component / @Controller 等并注册
        SpringApplication.run(Application.class, args);
    }
}

// 注意：这里把 Controller 放在同一文件中是为了演示方便
// 实际项目里每个类一个文件
// @RestController = @Controller + @ResponseBody
// 后者让方法返回值直接作为 HTTP 响应体（自动 JSON 序列化）
@RestController
class HelloController {

    // @GetMapping("/hello") 把 GET /hello 路由到这个方法
    // 方法返回 String，会被当作纯文本响应
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    // @PathVariable 把 URL 里的 {name} 绑定到方法参数
    // 访问 /greet/Alice → name = "Alice"
    @GetMapping("/greet/{name}")
    public String greet(@PathVariable String name) {
        return "Hi, " + name + " !";
    }
}
