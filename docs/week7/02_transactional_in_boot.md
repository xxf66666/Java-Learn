# Week 7 §02 · 在 Spring Boot 项目里实战事务

> 把 Week 5 学的 `@Transactional` 知识落地到博客项目里。

---

## 1. 一个真实场景：发文 + 计数

需求：发表一篇文章时，同时更新该用户的"已发文章数"。这两个动作必须**原子性**：要么都成功，要么都回滚。

```java
@Service
public class ArticleService extends ServiceImpl<ArticleMapper, Article> {

    private final UserService userService;

    public ArticleService(UserService userService) {
        this.userService = userService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long publish(Long userId, String title, String content) {
        // 1. 插文章
        Article a = new Article();
        a.setUserId(userId);
        a.setTitle(title);
        a.setContent(content);
        this.save(a);

        // 2. 更新用户的发文数
        User user = userService.getById(userId);
        if (user == null) throw new BusinessException(40401, "用户不存在");
        user.setArticleCount(user.getArticleCount() + 1);
        userService.updateById(user);

        return a.getId();
    }
}
```

**测试事务回滚**：在第二步后故意 `throw new RuntimeException("boom")`，看 article 表里是否没插入。

---

## 2. `rollbackFor = Exception.class` 的重要性

```java
@Transactional                                  // ❌ 默认只回滚 RuntimeException
public void buggy() throws IOException { ... }

@Transactional(rollbackFor = Exception.class)   // ✅ 所有 Exception 都回滚
public void safer() throws IOException { ... }
```

**实操**：写 `@Transactional` 时**几乎永远**带 `rollbackFor = Exception.class`。

---

## 3. 只读事务 `readOnly = true`

```java
@Transactional(readOnly = true)
public Page<Article> listPage(int page, int size) { ... }
```

只读事务能让数据库跳过一些锁机制，**纯查询方法都加上**。

---

## 4. 自调用失效的解决方案

```java
@Service
public class ArticleService {

    public void publishAndNotify() {
        this.publish(...);          // ❌ this 不走代理，事务失效
    }

    @Transactional
    public Long publish(...) { ... }
}
```

**解决方案 1：注入自己**

```java
@Service
public class ArticleService {
    @Autowired
    private ArticleService self;             // 注入自己

    public void publishAndNotify() {
        self.publish(...);                    // ✅ 走代理
    }

    @Transactional
    public Long publish(...) { ... }
}
```

**解决方案 2：用 AopContext**

```java
@Service
@EnableAspectJAutoProxy(exposeProxy = true)   // 主启动类上
public class ArticleService {
    public void publishAndNotify() {
        ((ArticleService) AopContext.currentProxy()).publish(...);
    }
}
```

---

## 5. 几个常见的失效错误

| 错误写法 | 为什么失效 | 修正 |
|---------|-----------|------|
| `this.method()` 自调用 | 不走代理 | 注入 self |
| `private @Transactional method()` | 代理只拦 public | 改 public |
| try-catch 吞了异常 | 没抛出，事务不知道要回滚 | 重抛 / 手动 `setRollbackOnly` |
| 方法不是 Spring Bean | 不在容器管理 | 改为 @Service |
| `rollbackFor` 没写，方法抛 checked 异常 | 默认不回滚 | 加 `rollbackFor = Exception.class` |

---

## 6. `REQUIRES_NEW` 实战：操作日志

```java
@Service
public class OperationLogService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String op, String detail) {
        // 新事务，独立 commit
        logMapper.insert(new OperationLog(op, detail));
    }
}

@Service
public class OrderService {

    @Autowired
    private OperationLogService logService;

    @Transactional
    public void createOrder(...) {
        orderMapper.insert(...);
        try {
            logService.log("CREATE_ORDER", "id=...");
        } catch (Exception e) {
            log.error("写日志失败但不影响主事务", e);
            // 不重抛
        }
    }
}
```

如果日志写失败抛异常，主事务也**不会回滚**——因为日志是独立事务。

---

## 7. 自查

- [ ] 在博客项目里写 `publish` 方法，故意在第二步抛异常，验证第一步也回滚
- [ ] 把 `rollbackFor = Exception.class` 去掉，让方法抛 `IOException`，看是否仍回滚（不会）
- [ ] 写一个"自调用失效"的例子，再用 self 注入修复
- [ ] 写 `REQUIRES_NEW` 的操作日志，让日志失败不影响主流程

## 代码示例

→ [`code/week7/blog-backend/`](../../code/week7/blog-backend/)
