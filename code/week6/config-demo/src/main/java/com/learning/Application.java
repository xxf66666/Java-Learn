package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// @ConfigurationProperties: 把一组配置批量绑定到对象字段
import org.springframework.boot.context.properties.ConfigurationProperties;
// @EnableConfigurationProperties: 启用 @ConfigurationProperties 类（不加它配置类不会被注册）
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
// 启用 MailProps 作为配置 Bean
// （MailProps 本身没标 @Component，靠这里激活）
@EnableConfigurationProperties(MailProps.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

/**
 * 配置属性类：自动从 yml 加载 mail.* 配置
 *
 * prefix = "mail" 让 mail.host → host 字段, mail.port → port 字段
 * 推荐这种"一组配置一个对象"的方式，比散落的 @Value 整洁
 */
@ConfigurationProperties(prefix = "mail")
class MailProps {
    private String host;
    private int port;
    private String username;
    private List<String> recipients;     // yml 里的列表会自动转 List

    // ⚠️ @ConfigurationProperties 需要 getter / setter（默认走 setter 注入）
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<String> getRecipients() { return recipients; }
    public void setRecipients(List<String> recipients) { this.recipients = recipients; }
}

/**
 * 演示接口：返回当前生效的 profile + 邮件配置
 */
@RestController
class InfoController {

    // 注入：MailProps Bean + Environment（Spring 提供的环境抽象）
    private final MailProps mail;
    private final org.springframework.core.env.Environment env;

    InfoController(MailProps mail, org.springframework.core.env.Environment env) {
        this.mail = mail;
        this.env = env;
    }

    @GetMapping("/info")
    public Object info() {
        // LinkedHashMap 保留 put 顺序，输出 JSON 时字段顺序固定
        // 这里用匿名内部类语法（双大括号）一次写多个 put
        return new java.util.LinkedHashMap<>() {{
            put("activeProfiles", env.getActiveProfiles());      // 当前激活的 profile（如 dev）
            put("mailHost", mail.getHost());
            put("mailPort", mail.getPort());
            put("mailUser", mail.getUsername());
            put("mailRecipients", mail.getRecipients());
        }};
    }
}
