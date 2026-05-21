package com.learning.blog;

// MyBatis-Plus 用的 Mapper 扫描注解
// 告诉 MyBatis 去哪个包找 Mapper 接口（不然要在每个 Mapper 上加 @Mapper）
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.learning.blog.mapper")     // 扫描这个包下所有 Mapper 接口
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
