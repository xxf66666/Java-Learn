package com.learning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Spring Cache 注解
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
// @EnableCaching: 开启 Spring Cache 功能（不加 @Cacheable 不生效）
import org.springframework.cache.annotation.EnableCaching;
// Spring Data Redis 自动配的 RedisTemplate 子类，key 和 value 都是 String
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableCaching       // ⭐ 开启缓存注解功能
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

/** 模拟一个慢查询，演示缓存效果 */
@Service
class ProductService {

    // 模拟数据库的 Map
    private final Map<Long, String> store = new ConcurrentHashMap<>(Map.of(
        1L, "iPhone", 2L, "iPad", 3L, "MacBook"
    ));

    // 计数器：每"打数据库"一次 +1，用来证明缓存生效
    private final AtomicInteger dbHit = new AtomicInteger();

    /**
     * @Cacheable: 进入方法前先查缓存
     *   - 命中 → 直接返回缓存值，不进方法体
     *   - 未命中 → 跑方法体，把返回值放进缓存
     * value: 缓存名（对应 Redis 里的 key 前缀）
     * key: 缓存 key 的 SpEL 表达式；#id 表示用方法参数 id
     * unless: 满足条件时**不**缓存（这里：返回 null 不缓存）
     */
    @Cacheable(value = "product", key = "#id", unless = "#result == null")
    public String getById(Long id) {
        // 计数 +1，证明这次走了真实方法
        dbHit.incrementAndGet();

        // 模拟慢查询：sleep 500ms
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        return store.get(id);
    }

    /**
     * @CacheEvict: 方法跑完后清缓存
     * 同样用 #id 表达式定位要清的 key
     */
    @CacheEvict(value = "product", key = "#id")
    public void update(Long id, String name) {
        store.put(id, name);
    }

    public int getDbHit() { return dbHit.get(); }
}

@RestController
@RequestMapping("/api")
class DemoController {

    // @Autowired 字段注入：演示用，构造器注入更推荐
    @Autowired ProductService service;
    @Autowired StringRedisTemplate redis;

    /** 演示：第一次慢、第二次命中缓存快 */
    @GetMapping("/product/{id}")
    public Map<String, Object> getProduct(@PathVariable Long id) {
        long t = System.currentTimeMillis();
        String name = service.getById(id);

        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("durationMs", System.currentTimeMillis() - t);    // 耗时
        m.put("dbHitTotal", service.getDbHit());                 // 历史打 DB 次数
        return m;
    }

    /** 改商品 → 通过 @CacheEvict 清缓存 */
    @PostMapping("/product/{id}")
    public Map<String, Object> updateProduct(@PathVariable Long id, @RequestParam String name) {
        service.update(id, name);
        return Map.of("ok", true);
    }

    /** 直接玩 String 类型的 Redis 操作 */
    @PostMapping("/cache/{key}")
    public Map<String, Object> set(@PathVariable String key, @RequestParam String value) {
        // opsForValue 操作 String 类型 key
        // 第三参 Duration.ofMinutes(10) 设过期时间
        redis.opsForValue().set(key, value, Duration.ofMinutes(10));
        return Map.of("ok", true);
    }

    @GetMapping("/cache/{key}")
    public Map<String, Object> get(@PathVariable String key) {
        return Map.of("value", redis.opsForValue().get(key));
    }
}
