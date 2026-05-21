package com.learning.repo;

// @Repository 是 Spring 的"模板注解"，等价于 @Component
// 语义化：表明这是"数据访问层" Bean
// Spring 启动时扫描到带 @Repository 的类会自动 new 实例放进容器
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
// AtomicLong：线程安全的 long，多线程下递增不会丢
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存版用户仓库。生产环境会接 MyBatis-Plus / JPA。
 * 这里用 Map 模拟数据库表。
 */
@Repository
public class UserRepo {

    // key = userId, value = userName
    // final 锁住 Map 引用，但 Map 内容可变
    private final Map<Long, String> store = new HashMap<>();

    // 自增 id 生成器：AtomicLong.incrementAndGet 原子地 +1
    private final AtomicLong seq = new AtomicLong();

    /** 插入并返回新 id */
    public Long save(String name) {
        // 先生成 id（先 +1 后取值）
        long id = seq.incrementAndGet();
        // 存进 Map
        store.put(id, name);
        return id;
    }

    /** 查找 */
    public Optional<String> findById(long id) {
        // ofNullable: null 包成 empty，非 null 包成 of
        return Optional.ofNullable(store.get(id));
    }

    /** 总数 */
    public int count() { return store.size(); }
}
