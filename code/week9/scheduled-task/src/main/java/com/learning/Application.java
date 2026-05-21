package com.learning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@Component
class DemoJobs {
    private static final Logger log = LoggerFactory.getLogger(DemoJobs.class);

    /** 每 10 秒跑一次 */
    @Scheduled(cron = "0/10 * * * * ?")
    public void everyTenSeconds() {
        log.info("⏰ {} 每 10 秒任务", LocalDateTime.now());
    }

    /** 每分钟跑一次 */
    @Scheduled(fixedRate = 60_000)
    public void perMinute() {
        log.info("⏰ {} 每分钟任务", LocalDateTime.now());
    }

    /** 真实日报：每天凌晨 2 点（学习时改成 cron = "0/30 * * * * ?" 观察） */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyReport() {
        log.info("📊 生成日报...");
    }
}
