package com.learning.erp;

// MyBatis-Plus 的 Mapper 扫描注解
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// 开启 @Async 异步方法支持
import org.springframework.scheduling.annotation.EnableAsync;
// 开启 @Scheduled 定时任务支持
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ERP 主启动类，唯一一个有 main 方法的入口
 *
 * 关键注解：
 *  - @SpringBootApplication: 启动 Spring Boot
 *  - scanBasePackages: 必须扫到所有模块的根包 com.learning.erp
 *      默认只扫主类所在包及子包，但 admin 子模块包名不能覆盖所有
 *  - @MapperScan: 扫描 Mapper 接口
 *      用 ** 通配符匹配各模块的 mapper 子包：com.learning.erp.system.user.mapper / .business.stock.mapper 等
 *  - @EnableAsync: 让 @Async 注解生效（操作日志异步入库需要）
 *  - @EnableScheduling: 让 @Scheduled 注解生效（如果有定时任务的话）
 */
@SpringBootApplication(scanBasePackages = "com.learning.erp")
@MapperScan("com.learning.erp.**.mapper")
@EnableAsync
@EnableScheduling
public class AdminApplication {

    public static void main(String[] args) {
        // Spring Boot 启动主流程：
        //  1. 创建 ApplicationContext
        //  2. 启动内嵌 Tomcat
        //  3. 扫描注册所有模块的 Bean
        //  4. 跑 @PostConstruct 初始化
        //  5. 等待请求
        SpringApplication.run(AdminApplication.class, args);
    }
}
