package com.learning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// @EnableScheduling: 开启定时任务功能（@Scheduled 才会生效）
import org.springframework.scheduling.annotation.EnableScheduling;
// @Scheduled: 把方法标成定时任务
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling      // ⭐ 没有这个，下面的 @Scheduled 不工作
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

/** 定时任务 Bean */
@Component
class DemoJobs {

    private static final Logger log = LoggerFactory.getLogger(DemoJobs.class);

    /**
     * 用 cron 表达式定义触发时机
     * "0/10 * * * * ?" 解读：
     *   秒 0/10 = 从 0 秒开始，每 10 秒一次
     *   分 *   = 任意分
     *   时 *
     *   日 *
     *   月 *
     *   周 ?   = 不指定（cron 6 位 / 7 位都有，Spring 用 6 位）
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void everyTenSeconds() {
        log.info("⏰ {} 每 10 秒任务", LocalDateTime.now());
    }

    /**
     * fixedRate: 上次开始后多少毫秒下次开始（不管上次跑了多久）
     * 60_000 = 60 秒
     */
    @Scheduled(fixedRate = 60_000)
    public void perMinute() {
        log.info("⏰ {} 每分钟任务", LocalDateTime.now());
    }

    /**
     * cron "0 0 2 * * ?" = 每天凌晨 2:00:00
     * 学习时可以改成 "0/30 * * * * ?" 每 30 秒一次，方便看效果
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyReport() {
        log.info("📊 生成日报...");
    }
}
