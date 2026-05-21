package com.learning.service;

import com.learning.repo.UserRepo;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepo repo;

    @Value("${app.greeting:Hello}")
    private String greeting;

    public UserService(UserRepo repo) {
        this.repo = repo;
    }

    @PostConstruct
    public void init() {
        log.info("UserService 启动，预置一个 admin");
        repo.save("admin");
    }

    public Long register(String name) {
        Long id = repo.save(name);
        log.info("用户注册：id={}, name={}", id, name);
        return id;
    }

    public String greet(long id) {
        return repo.findById(id)
                   .map(name -> greeting + ", " + name)
                   .orElse("用户不存在");
    }

    public int totalUsers() { return repo.count(); }
}
