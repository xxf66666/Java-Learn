# Week 4 §01 · JUnit 5 + Logback

> 目标：能为业务代码写单元测试，能用 Logback 替代 `System.out.println` 输出日志。

---

## 1. JUnit 5

### 1.1 引入依赖

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>
```

### 1.2 第一个测试

```java
// src/main/java/com/learning/Calculator.java
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public int divide(int a, int b) {
        if (b == 0) throw new IllegalArgumentException("除数不能为 0");
        return a / b;
    }
}

// src/test/java/com/learning/CalculatorTest.java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    Calculator calc;

    @BeforeEach
    void setup() {
        calc = new Calculator();
    }

    @Test
    void testAdd() {
        assertEquals(5, calc.add(2, 3));
    }

    @Test
    void testDivide() {
        assertEquals(5, calc.divide(10, 2));
    }

    @Test
    void testDivideByZero() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> calc.divide(10, 0));
        assertTrue(ex.getMessage().contains("不能为 0"));
    }
}
```

### 1.3 常用注解

| 注解 | 作用 |
|------|------|
| `@Test` | 标记一个测试方法 |
| `@BeforeEach` | 每个 `@Test` 前都跑（初始化） |
| `@AfterEach` | 每个 `@Test` 后都跑（清理） |
| `@BeforeAll` | 整个类只跑一次（必须 static） |
| `@DisplayName("说明")` | 在 IDEA 测试报告里更友好显示 |
| `@Disabled` | 临时跳过 |
| `@RepeatedTest(5)` | 重复跑 5 次 |
| `@ParameterizedTest` | 参数化测试 |

### 1.4 常用断言

```java
assertEquals(expected, actual);
assertTrue(condition);
assertFalse(condition);
assertNull(x);
assertNotNull(x);
assertThrows(IllegalArgumentException.class, () -> ...);
assertArrayEquals(expected, actual);

// 多个断言一起执行（不会因为前一个失败就停）
assertAll(
    () -> assertEquals(5, calc.add(2, 3)),
    () -> assertTrue(calc.add(1, 1) > 0)
);
```

### 1.5 参数化测试

```java
@ParameterizedTest
@CsvSource({
    "1, 2, 3",
    "10, 20, 30",
    "-5, 5, 0"
})
void testAddParam(int a, int b, int expected) {
    assertEquals(expected, calc.add(a, b));
}
```

---

## 2. Mockito：模拟依赖

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.12.0</version>
    <scope>test</scope>
</dependency>
```

```java
import static org.mockito.Mockito.*;

interface UserRepo {
    User findById(long id);
}

class UserService {
    private final UserRepo repo;
    public UserService(UserRepo repo) { this.repo = repo; }
    public String greet(long id) { return "Hi " + repo.findById(id).getName(); }
}

@Test
void testGreet() {
    UserRepo mock = mock(UserRepo.class);
    when(mock.findById(1L)).thenReturn(new User(1L, "Alice"));

    UserService svc = new UserService(mock);
    assertEquals("Hi Alice", svc.greet(1L));

    verify(mock).findById(1L);     // 校验 findById(1L) 被调过
}
```

Mockito 在测试 Service 层时极有用——**不依赖真数据库就能测**。

---

## 3. 日志：SLF4J + Logback

### 3.1 为什么不用 `System.out.println`

- 没法分级（DEBUG / INFO / WARN / ERROR）
- 不能配置输出到文件 / 远程
- 生产环境想关掉只能改代码

### 3.2 引入

```xml
<!-- 日志门面 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.13</version>
</dependency>
<!-- 实现 -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.5.6</version>
</dependency>
```

> **为什么有"门面 + 实现"两层**：SLF4J 是抽象接口，业务代码只依赖 SLF4J；底层实现可以换成 Logback / Log4j2，不用改业务代码。

### 3.3 在代码里用

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void createUser(String name) {
        log.debug("调试日志：参数 name={}", name);
        log.info("创建用户：{}", name);
        log.warn("名字过长警告：{}", name);
        try {
            // ...
        } catch (Exception e) {
            log.error("创建用户失败", e);     // 带异常打印完整栈
        }
    }
}
```

**关键技巧**：用 `{}` 占位符而不是字符串拼接

```java
log.info("user " + name + " created");     // ❌ 即使 INFO 关了，拼接也发生
log.info("user {} created", name);          // ✅ 关了就不拼接
```

### 3.4 配置文件 `src/main/resources/logback.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/app-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 单独调节某个包的日志级别 -->
    <logger name="com.learning" level="DEBUG"/>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

**日志级别**：`TRACE < DEBUG < INFO < WARN < ERROR`。生产一般 INFO；本地开发 DEBUG。

---

## 4. 自查

- [ ] 用 JUnit 5 给 `Calculator` 写 5+ 测试，包括异常断言
- [ ] 写一个 `@ParameterizedTest`
- [ ] 用 Mockito mock 一个接口
- [ ] 把项目里所有 `System.out` 换成 `log.info("xxx {}", arg)`
- [ ] 写 `logback.xml`，让 DEBUG 级别只在自己包里输出

## 代码示例

→ [`code/week4/junit-demo/`](../../code/week4/junit-demo/)
