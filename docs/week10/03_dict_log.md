# Week 10 §03 · 数据字典 + 操作日志

> 这两个是 ERP 每个模块都会复用的"基础设施"，做一次后面省一万次。

---

## 1. 数据字典：枚举值的统一管理

### 场景

性别（0 男 1 女）、单据状态（待审 / 已审 / 已作废）、币种（CNY / USD / EUR）...

每个模块都用，写死在代码里 → 改了要发版；存到字典表 → 一改全局生效。

### 两表设计

```sql
-- 字典类型
CREATE TABLE sys_dict_type (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(64) NOT NULL,        -- "性别"
    code        VARCHAR(64) NOT NULL UNIQUE,  -- "gender"
    remark      VARCHAR(255),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT  DEFAULT 0
);

-- 字典项
CREATE TABLE sys_dict_item (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    dict_code   VARCHAR(64) NOT NULL,         -- 关联 sys_dict_type.code
    label       VARCHAR(64) NOT NULL,         -- "男" / "女"
    value       VARCHAR(64) NOT NULL,         -- "0" / "1"
    sort        INT DEFAULT 0,
    status      TINYINT DEFAULT 1,
    INDEX idx_code (dict_code)
);
```

### 接口

```java
@GetMapping("/api/dict/{code}")
public Result<List<DictItem>> getByCode(@PathVariable String code) {
    return Result.ok(dictService.listByCode(code));
}
```

前端启动时一次性拉取常用字典缓存到本地（vuex / pinia），表单 / 列表渲染时根据 value 找对应 label。

### 后端缓存

```java
@Cacheable(value = "dict", key = "#code")
public List<DictItem> listByCode(String code) {
    return dictItemMapper.selectList(...);
}

@CacheEvict(value = "dict", key = "#code")
public void update(DictItem item) { ... }
```

---

## 2. 操作日志：自动记录"谁在什么时候做了什么"

### 表

```sql
CREATE TABLE sys_operation_log (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT,
    username    VARCHAR(64),
    module      VARCHAR(64),                  -- "用户管理"
    operation   VARCHAR(64),                   -- "新增用户"
    method      VARCHAR(255),                  -- 方法名
    params      TEXT,                           -- 请求参数（JSON）
    result      TEXT,                           -- 返回值（JSON，可裁剪）
    ip          VARCHAR(64),
    user_agent  VARCHAR(255),
    duration_ms BIGINT,
    success     TINYINT DEFAULT 1,
    error_msg   VARCHAR(1000),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_created (created_at)
);
```

### 自定义注解

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SysLog {
    String module() default "";
    String operation() default "";
}
```

### AOP 切面

```java
@Aspect
@Component
@Slf4j
public class SysLogAspect {

    @Autowired
    private OperationLogService logService;

    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint pjp, SysLog sysLog) throws Throwable {
        OperationLog opLog = new OperationLog();
        opLog.setModule(sysLog.module());
        opLog.setOperation(sysLog.operation());
        opLog.setMethod(pjp.getSignature().toShortString());
        opLog.setParams(serialize(pjp.getArgs()));
        opLog.setIp(getClientIp());
        // 当前用户
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            opLog.setUsername(auth.getName());
        }

        long t = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            opLog.setSuccess(1);
            opLog.setResult(serialize(result));
            return result;
        } catch (Throwable e) {
            opLog.setSuccess(0);
            opLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            opLog.setDurationMs(System.currentTimeMillis() - t);
            opLog.setCreatedAt(LocalDateTime.now());
            // 异步入库，避免拖慢接口
            asyncSave(opLog);
        }
    }

    private void asyncSave(OperationLog op) {
        CompletableFuture.runAsync(() -> {
            try { logService.save(op); }
            catch (Exception e) { log.error("写日志失败", e); }
        });
    }
}
```

### 使用

```java
@SysLog(module = "用户", operation = "新增")
@PostMapping
public Result<Long> create(@RequestBody UserCreateReq req) { ... }
```

---

## 3. 异步执行：用 `@Async` 把日志入库剥离主流程

```java
@SpringBootApplication
@EnableAsync
public class AdminApplication { ... }

@Service
public class OperationLogService {
    @Async
    public void saveAsync(OperationLog op) {
        baseMapper.insert(op);
    }
}
```

避免日志慢拖累接口。生产更彻底的做法：发到 MQ → 单独消费者入库。

---

## 4. 安全：日志里别记密码

```java
@SysLog(module = "用户", operation = "改密码")
public void changePassword(...) {}
```

切面里序列化参数时，**过滤掉密码、token、身份证、银行卡号**这种敏感字段。简单做法：

```java
private String serialize(Object[] args) {
    // 把可能含密码的字段统一加 @JsonIgnore，或自定义 ObjectMapper 配置
}
```

---

## 5. 自查

- [ ] 设计字典两表，跑通"按 code 查字典项列表"接口
- [ ] 字典查询加 Redis 缓存
- [ ] 实现 `@SysLog` 注解 + AOP，标了的方法被自动记录
- [ ] 操作日志异步入库（`@Async`）
- [ ] 在切面里过滤掉密码字段
