package com.learning.service;

import com.learning.repo.UserRepo;
// @PostConstruct 在 Bean 初始化完成后自动跑（依赖注入完成之后）
// 注意：JDK 9+ 需要单独依赖 jakarta.annotation 或 javax.annotation
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// @Value 把配置文件的属性注入字段
import org.springframework.beans.factory.annotation.Value;
// @Service 也是 @Component 别名，语义化标记"业务层"
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // SLF4J Logger，约定每类一个 static final
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    // final 字段配合构造器注入：UserService 一旦创建就锁定 repo 不变
    private final UserRepo repo;

    // @Value("${...}"): 从 Spring 配置（properties / yml）取值
    // ":Hello" 是默认值：配置没找到时用 Hello
    @Value("${app.greeting:Hello}")
    private String greeting;

    // 构造器注入：只有一个构造器时 @Autowired 可省略
    // Spring 启动时看 UserService 需要 UserRepo，去容器找一个传进来
    public UserService(UserRepo repo) {
        this.repo = repo;
    }

    // @PostConstruct：Bean 创建 + 依赖注入完成后立刻执行
    // 用于初始化数据 / 预热缓存等
    @PostConstruct
    public void init() {
        log.info("UserService 启动，预置一个 admin");
        repo.save("admin");
    }

    /** 注册新用户 */
    public Long register(String name) {
        Long id = repo.save(name);
        log.info("用户注册：id={}, name={}", id, name);
        return id;
    }

    /** 根据 id 打招呼，找不到给默认消息 */
    public String greet(long id) {
        return repo.findById(id)
                   .map(name -> greeting + ", " + name)     // map: 有值则变换
                   .orElse("用户不存在");                    // orElse: 没值给默认
    }

    public int totalUsers() { return repo.count(); }
}
