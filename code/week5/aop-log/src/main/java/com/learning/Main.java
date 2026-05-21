package com.learning;

import com.learning.service.CalcService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
// @EnableAspectJAutoProxy 打开 AOP 自动代理：让 @Aspect 切面真正生效
import org.springframework.context.annotation.EnableAspectJAutoProxy;

public class Main {

    /**
     * 内嵌的配置类（也可以单独抽文件，这里图省事）
     * 用 static 因为内嵌在 Main 里，避免依赖外部实例
     */
    @Configuration
    @ComponentScan("com.learning")          // 扫描业务 Bean
    @EnableAspectJAutoProxy                   // ⭐ 开启 AOP，没有它切面不工作
    static class AppConfig {}

    public static void main(String[] args) {
        // 启动 Spring 容器
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        // 拿到的不是"真实 CalcService"，而是 Spring 创建的代理对象（CGLib 生成）
        // 代理对象转发方法调用时会先跑切面，再跑真实方法
        CalcService calc = ctx.getBean(CalcService.class);

        // 这一次调用会触发：
        //   1. SERVICE 切点：日志带 [SERVICE] 标签
        // 看 LogAspect 输出可以看到入参 / 返回 / 耗时
        System.out.println("add: " + calc.add(2, 3));

        // 这一次会触发两个切点（SERVICE + @LogTime），日志会有两组
        System.out.println("fib(20): " + calc.fib(20));

        // 异常分支：日志会用 error 级打印
        try {
            calc.divide(10, 0);
        } catch (Exception e) {
            System.out.println("捕获: " + e.getMessage());
        }

        ctx.close();
    }
}
