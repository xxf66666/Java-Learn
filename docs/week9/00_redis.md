# Week 9 §00 · Redis

> Redis 是内存数据库，读写极快，工作中主要做：**缓存**、**分布式锁**、**排行榜**、**计数器**、**会话**。

---

## 1. 启动 Redis

```bash
docker run -d --name redis7 -p 6379:6379 redis:7

# 测试
docker exec -it redis7 redis-cli PING
# PONG
```

---

## 2. 五大基础类型

| 类型 | 典型场景 | 命令样例 |
|------|---------|----------|
| **String** | 缓存对象 / 计数器 / 分布式锁 | `SET key val EX 60` `INCR cnt` |
| **Hash** | 缓存对象（按字段更新） | `HSET user:1 name Alice age 20` |
| **List** | 消息队列 / 最近 N 条 | `LPUSH q msg` `RPOP q` |
| **Set** | 去重 / 标签 / 共同好友 | `SADD tags java spring` `SINTER` |
| **ZSet** | 排行榜（有序集合） | `ZADD rank 100 alice` `ZRANGE rank 0 9` |

---

## 3. 在 Spring Boot 里集成

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 3000ms
```

---

## 4. `StringRedisTemplate`：最常用

```java
@Autowired
private StringRedisTemplate redis;

// String
redis.opsForValue().set("k", "v");
redis.opsForValue().set("k", "v", Duration.ofMinutes(10));
String v = redis.opsForValue().get("k");
redis.delete("k");

// Hash
redis.opsForHash().put("user:1", "name", "Alice");
redis.opsForHash().get("user:1", "name");

// List
redis.opsForList().leftPush("queue", "msg1");
redis.opsForList().rightPop("queue");

// Set
redis.opsForSet().add("tags", "java", "spring");
redis.opsForSet().members("tags");

// ZSet
redis.opsForZSet().add("rank", "alice", 100);
redis.opsForZSet().reverseRangeWithScores("rank", 0, 9);
```

> 想存 Java 对象？用 `RedisTemplate<String, Object>` + Jackson 序列化器。

---

## 5. 配置序列化器

默认的序列化器（JDK 自带）输出乱七八糟，控制台用 `redis-cli` 看不懂。改成 JSON：

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> t = new RedisTemplate<>();
        t.setConnectionFactory(cf);

        t.setKeySerializer(new StringRedisSerializer());
        t.setHashKeySerializer(new StringRedisSerializer());

        var jsonSer = new Jackson2JsonRedisSerializer<>(Object.class);
        t.setValueSerializer(jsonSer);
        t.setHashValueSerializer(jsonSer);

        return t;
    }
}
```

---

## 6. Spring Cache 注解（懒人模式）

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

```java
@EnableCaching
@Configuration
class CacheConfig {}
```

```java
@Service
public class ProductService {

    @Cacheable(value = "product", key = "#id", unless = "#result == null")
    public Product getById(Long id) {
        return productMapper.selectById(id);          // 第一次会真查，结果缓存到 Redis
    }

    @CacheEvict(value = "product", key = "#p.id")
    public void update(Product p) {
        productMapper.updateById(p);
    }

    @CacheEvict(value = "product", allEntries = true)
    public void clearAll() { ... }
}
```

---

## 7. 缓存三大问题

### 7.1 缓存穿透

**问题**：查不存在的 key，每次都打到数据库。

**对策**
- 缓存 null 值（短 TTL）：`set("user:999", "NULL", 60s)`
- Bloom Filter 拦截

### 7.2 缓存击穿

**问题**：某热点 key 突然过期，瞬间大量请求打数据库。

**对策**
- 互斥锁：第一个进来的线程查数据库，其他等
- 永不过期 + 异步刷新

### 7.3 缓存雪崩

**问题**：大量 key 同时过期 / Redis 整体挂掉。

**对策**
- 过期时间加随机抖动（基础时间 + random）
- Redis 集群 / 哨兵保证可用性
- 限流 + 熔断保护数据库

---

## 8. 分布式锁（一行代码版）

```java
public boolean tryLock(String key, String value, long expireSec) {
    Boolean ok = redis.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(expireSec));
    return Boolean.TRUE.equals(ok);
}

public void unlock(String key, String value) {
    // 用 Lua 保证 "判断 + 删除" 原子，防止删别人的锁
    String lua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    redis.execute(new DefaultRedisScript<>(lua, Long.class), List.of(key), value);
}
```

生产建议直接用 **Redisson**（专业的分布式锁库），更安全。

---

## 9. 自查

- [ ] Docker 跑起 Redis，用 `redis-cli` 测连接
- [ ] Spring Boot 项目里用 `StringRedisTemplate` 写一个简单 KV 操作
- [ ] 用 `@Cacheable` 给一个查询加缓存，看打印的 SQL：第一次有，第二次没有
- [ ] 解释缓存穿透 / 击穿 / 雪崩 + 每种的对策
- [ ] 写一个简单的分布式锁（`setIfAbsent`）

## 代码示例

→ [`code/week9/redis-cache/`](../../code/week9/redis-cache/)
