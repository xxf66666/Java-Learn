package com.learning;

import com.learning.config.AppConfig;
import com.learning.controller.UserController;
// AnnotationConfigApplicationContext 是 Spring 容器实现之一：基于 Java 配置类启动
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        // 启动 Spring 容器：传入配置类 AppConfig
        // Spring 启动后会做：
        //   1. 解析 AppConfig 上的注解，发现 @ComponentScan
        //   2. 扫描 com.learning 包，找到所有 @Component / @Service / @Repository / @Controller
        //   3. 实例化每个 Bean（用反射调构造器）
        //   4. 解决依赖：注入 @Autowired 字段 / 构造器参数
        //   5. 跑 @PostConstruct 初始化方法
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        // 从容器拿一个 Bean
        // 实际开发中很少这样手动 getBean，都用 @Autowired 让 Spring 注进来
        // 这里只是 main 方法不在容器里，必须手动获取一次入口
        UserController controller = ctx.getBean(UserController.class);

        // 调 Controller 方法
        System.out.println(controller.register("Alice"));
        System.out.println(controller.register("Bob"));

        // 1 是 admin（@PostConstruct 预置的）
        System.out.println(controller.greet(1));
        // 2 是 Alice（注册的第一个用户）
        System.out.println(controller.greet(2));
        // 999 不存在，返回"用户不存在"
        System.out.println(controller.greet(999));

        // 关闭容器：触发 Bean 的销毁回调（@PreDestroy）
        ctx.close();
    }
}
