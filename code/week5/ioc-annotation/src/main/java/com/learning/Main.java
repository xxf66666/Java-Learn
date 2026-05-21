package com.learning;

import com.learning.config.AppConfig;
import com.learning.controller.UserController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        // 启动 Spring 容器
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        // 从容器拿 Bean（实际使用中很少这样手动 get，都靠依赖注入）
        UserController controller = ctx.getBean(UserController.class);

        System.out.println(controller.register("Alice"));
        System.out.println(controller.register("Bob"));
        System.out.println(controller.greet(1));
        System.out.println(controller.greet(2));
        System.out.println(controller.greet(999));

        ctx.close();
    }
}
