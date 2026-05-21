# Week 5 §00 · IoC / DI

> Spring 的核心思想就一句话：**"对象不要自己 new，由容器统一管理"**。

---

## 1. 没有 IoC 的代码长什么样

```java
public class UserController {
    private UserService service = new UserService();        // 自己 new

    public String greet(long id) {
        return service.greet(id);
    }
}

public class UserService {
    private UserRepo repo = new UserRepo();                  // 自己 new

    public String greet(long id) {
        User u = repo.findById(id);
        return "Hi " + u.getName();
    }
}

public class UserRepo {
    public User findById(long id) { /* JDBC */ }
}
```

**痛点**
- `UserController` 写死了用 `UserService`，想换实现要改代码
- `UserService` 写死了用 `UserRepo`，**测试时不能 mock**
- new 的顺序、生命周期都要自己管
- 改一个依赖牵一发动全身

---

## 2. 有 IoC 之后

```java
@Component
public class UserController {
    private final UserService service;

    public UserController(UserService service) {        // 构造器注入
        this.service = service;
    }
}

@Component
public class UserService {
    private final UserRepo repo;
    public UserService(UserRepo repo) {
        this.repo = repo;
    }
}

@Component
public class UserRepo {
    public User findById(long id) { ... }
}
```

**变化**
- 没有 `new`，全是构造器接收依赖
- 谁来 new ？**Spring 容器**：它扫描所有 `@Component`，自己 new 出来、按需注入
- 想替换 `UserRepo` 实现？写一个新的 `@Component` 实现同一个接口，改配置就行
- 想测试？测试时塞一个 Mock 进去

---

## 3. 核心概念

| 概念 | 含义 |
|------|------|
| **IoC**（Inversion of Control，控制反转） | 控制权从"我自己 new" 反转到"容器 new"。是一种思想。 |
| **DI**（Dependency Injection，依赖注入） | IoC 的具体实现：容器把依赖"注入"到对象里。 |
| **Bean** | 被 Spring 容器管理的对象。可以理解成"被 new 出来放在容器里的对象"。 |
| **ApplicationContext** | Spring 容器本身。"装所有 Bean 的池子"。 |

---

## 4. 三种声明 Bean 的方式

### 方式 1：`@Component` 系列注解（最常用）

```java
@Component           // 通用
@Service             // Service 层（语义化，本质等于 @Component）
@Repository          // DAO 层
@Controller          // Web Controller
public class UserService { ... }
```

加上注解后，要让 Spring 扫描到：

```java
@Configuration
@ComponentScan(basePackages = "com.learning")
public class AppConfig { }
```

Spring Boot 里 `@SpringBootApplication` 自动开启扫描，**主启动类所在包及子包**都会被扫到。

### 方式 2：`@Configuration` + `@Bean`（用于第三方类）

第三方类（如 `DataSource`、`RestTemplate`）你改不了源码加 `@Component`，怎么办？写一个配置类：

```java
@Configuration
public class DataConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("...");
        return new HikariDataSource(cfg);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### 方式 3：XML 配置（老项目偶尔见到，新项目不用）

---

## 5. 三种依赖注入方式

### 方式 1：构造器注入（推荐 ✅）

```java
@Service
public class UserService {
    private final UserRepo repo;

    public UserService(UserRepo repo) {        // 单个构造器，Spring 自动注入
        this.repo = repo;
    }
}
```

**为什么推荐**
- 字段可以 `final` → 一旦注入永远不变
- 依赖**强制**指定（构造器签名清晰）
- 单元测试时直接 `new UserService(mockRepo)`

### 方式 2：Setter 注入

```java
@Service
public class UserService {
    private UserRepo repo;

    @Autowired
    public void setRepo(UserRepo repo) { this.repo = repo; }
}
```

什么时候用：**可选依赖**（`@Autowired(required = false)`）。

### 方式 3：字段注入（不推荐 ❌）

```java
@Service
public class UserService {
    @Autowired
    private UserRepo repo;        // 不需要构造器
}
```

**为什么不推荐**
- 字段不能 `final`
- 测试时只能反射注入
- 跟"依赖反转"的精神相违（隐藏了依赖关系）

> Spring 官方文档 + 阿里规约都建议**永远用构造器注入**。

---

## 6. `@Autowired` 的解析规则

```java
@Service
public class UserService {
    private final UserRepo repo;
    public UserService(UserRepo repo) { this.repo = repo; }
}
```

Spring 找 `UserRepo` 类型的 Bean：
1. 容器里只有一个 `UserRepo` → 直接注入
2. 有多个实现（如 `UserRepoMysql` / `UserRepoH2`）→ 报错，要 `@Qualifier` 指定

```java
public UserService(@Qualifier("userRepoMysql") UserRepo repo) { ... }
```

或在某个实现上加 `@Primary` 优先选它。

---

## 7. Bean 作用域 `@Scope`

```java
@Component
@Scope("singleton")             // 默认：整个容器只有一个实例
public class UserService { }

@Component
@Scope("prototype")              // 每次 getBean 都新 new
public class TempCounter { }
```

| scope | 含义 | 常用 |
|-------|------|------|
| `singleton` | 容器只一个实例（默认） | ✅ 90% Bean 是这个 |
| `prototype` | 每次都新建 | 有状态的辅助对象 |
| `request` | 每个 HTTP 请求一个 | Web 场景 |
| `session` | 每个 HTTP session 一个 | Web 场景 |

---

## 8. Bean 生命周期

```
1. 实例化（new）
2. 属性赋值（@Autowired）
3. Aware 回调（如 ApplicationContextAware）
4. BeanPostProcessor.before
5. @PostConstruct / InitializingBean.afterPropertiesSet
6. BeanPostProcessor.after
7. ←── 可用 ──→
8. @PreDestroy（容器关闭时）
```

工作中用到的就 `@PostConstruct`（初始化时跑一次）和 `@PreDestroy`（销毁时跑一次）。

```java
@Component
public class CacheService {
    @PostConstruct
    public void warmUp() { /* 启动时预热缓存 */ }

    @PreDestroy
    public void cleanup() { /* 关闭时清理 */ }
}
```

---

## 9. 配置参数注入 `@Value`

```java
@Component
public class MailService {
    @Value("${mail.host}")               // 从 application.yml / properties 取
    private String host;

    @Value("${mail.port:25}")             // 默认值 25
    private int port;
}
```

```yaml
# application.yml
mail:
  host: smtp.example.com
  port: 587
```

更复杂场景用 `@ConfigurationProperties`（Week 6 讲）。

---

## 10. 自查

- [ ] 用一段话解释 IoC 解决了什么问题
- [ ] 写一个三层结构（Controller / Service / Dao）的 Spring 项目（不必接 DB，DAO 用内存 List 即可）
- [ ] 用构造器注入完成所有依赖注入
- [ ] 解释 `@Component` / `@Service` / `@Repository` / `@Controller` 的关系（语义不同，本质一样）
- [ ] 用 `@Bean` 配置一个 `DataSource`
- [ ] 解释为什么字段注入不推荐
- [ ] 写一个有 `@PostConstruct` 的 Bean

## 代码示例

→ [`code/week5/ioc-annotation/`](../../code/week5/ioc-annotation/)
