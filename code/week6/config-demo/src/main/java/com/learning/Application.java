package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(MailProps.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@ConfigurationProperties(prefix = "mail")
class MailProps {
    private String host;
    private int port;
    private String username;
    private List<String> recipients;
    // getter/setter
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<String> getRecipients() { return recipients; }
    public void setRecipients(List<String> recipients) { this.recipients = recipients; }
}

@RestController
class InfoController {
    private final MailProps mail;
    private final org.springframework.core.env.Environment env;

    InfoController(MailProps mail, org.springframework.core.env.Environment env) {
        this.mail = mail;
        this.env = env;
    }

    @GetMapping("/info")
    public Object info() {
        return new java.util.LinkedHashMap<>() {{
            put("activeProfiles", env.getActiveProfiles());
            put("mailHost", mail.getHost());
            put("mailPort", mail.getPort());
            put("mailUser", mail.getUsername());
            put("mailRecipients", mail.getRecipients());
        }};
    }
}
