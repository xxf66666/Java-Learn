package com.learning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableCaching
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

/** 模拟一个慢查询，演示缓存效果 */
@Service
class ProductService {
    private final Map<Long, String> store = new ConcurrentHashMap<>(Map.of(
        1L, "iPhone", 2L, "iPad", 3L, "MacBook"
    ));
    private final AtomicInteger dbHit = new AtomicInteger();

    @Cacheable(value = "product", key = "#id", unless = "#result == null")
    public String getById(Long id) {
        dbHit.incrementAndGet();
        // 模拟慢查询
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        return store.get(id);
    }

    @CacheEvict(value = "product", key = "#id")
    public void update(Long id, String name) {
        store.put(id, name);
    }

    public int getDbHit() { return dbHit.get(); }
}

@RestController
@RequestMapping("/api")
class DemoController {
    @Autowired ProductService service;
    @Autowired StringRedisTemplate redis;

    @GetMapping("/product/{id}")
    public Map<String, Object> getProduct(@PathVariable Long id) {
        long t = System.currentTimeMillis();
        String name = service.getById(id);
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("durationMs", System.currentTimeMillis() - t);
        m.put("dbHitTotal", service.getDbHit());
        return m;
    }

    @PostMapping("/product/{id}")
    public Map<String, Object> updateProduct(@PathVariable Long id, @RequestParam String name) {
        service.update(id, name);
        return Map.of("ok", true);
    }

    // 直接玩 String 类型
    @PostMapping("/cache/{key}")
    public Map<String, Object> set(@PathVariable String key, @RequestParam String value) {
        redis.opsForValue().set(key, value, Duration.ofMinutes(10));
        return Map.of("ok", true);
    }

    @GetMapping("/cache/{key}")
    public Map<String, Object> get(@PathVariable String key) {
        return Map.of("value", redis.opsForValue().get(key));
    }
}
