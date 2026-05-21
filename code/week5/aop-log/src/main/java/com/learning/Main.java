package com.learning;

import com.learning.service.CalcService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

public class Main {

    @Configuration
    @ComponentScan("com.learning")
    @EnableAspectJAutoProxy            // 开启 AOP 自动代理
    static class AppConfig {}

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        CalcService calc = ctx.getBean(CalcService.class);

        System.out.println("add: " + calc.add(2, 3));
        System.out.println("fib(20): " + calc.fib(20));
        try {
            calc.divide(10, 0);
        } catch (Exception e) {
            System.out.println("捕获: " + e.getMessage());
        }

        ctx.close();
    }
}
