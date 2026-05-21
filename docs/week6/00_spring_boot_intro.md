# Week 6 §00 · Spring Boot 入门

> Spring Boot = Spring + 自动装配 + 内嵌容器 + 起步依赖。**让你 5 分钟跑起来一个 Web 应用**。

---

## 1. Spring vs Spring Boot

| 维度 | 纯 Spring | Spring Boot |
|------|----------|------------|
| 配置 | 大量 XML / 注解 | 几乎零配置（约定大于配置） |
| 部署 | 打 war 包 → 装 Tomcat | 内嵌 Tomcat，直接 `java -jar` |
| 依赖 | 一个个加，版本要自己对 | Starter 一键引入 + 版本统一 |
| 上手 | 难 | 简单 |

---

## 2. 创建项目

### 方式 A：Spring Initializr 网站

打开 <https://start.spring.io>，选：
- Project: Maven
- Language: Java
- Spring Boot: 3.3.x
- Java: 21
- Dependencies: Spring Web + Lombok

下载 → 解压 → IDEA 打开。

### 方式 B：IDEA 直接生成（Ultimate 才有）

`File → New → Project → Spring Initializr`

### 方式 C：直接复制本周示例

```bash
cp -r code/week6/hello-boot /tmp/my-app
cd /tmp/my-app && mvn spring-boot:run
```

---

## 3. 最简单的 Spring Boot 项目

### pom.xml

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.0</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

### 主启动类

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 一个 Controller

```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }
}
```

跑起来：`mvn spring-boot:run` → 浏览器访问 `http://localhost:8080/hello`。

---

## 4. `@SpringBootApplication` 拆解

```java
@SpringBootApplication
// ≈
@SpringBootConfiguration       // 等价于 @Configuration
@EnableAutoConfiguration       // 开启自动装配（核心！）
@ComponentScan                 // 扫描主类所在包及子包
```

**核心是 `@EnableAutoConfiguration`**：根据 classpath 里有什么依赖，**自动**配置对应的 Bean。

举例：
- classpath 里有 `spring-webmvc` → 自动配置 DispatcherServlet、内嵌 Tomcat
- classpath 里有 `spring-data-redis` → 自动配置 RedisTemplate
- classpath 里有 MySQL 驱动 + 你写了 `spring.datasource.url` → 自动配 HikariCP + DataSource

---

## 5. Starter 机制

Starter 就是"一组相关依赖 + 默认配置"的打包：

| Starter | 引入什么 |
|---------|---------|
| `spring-boot-starter-web` | Spring MVC + Tomcat + Jackson |
| `spring-boot-starter-data-jpa` | Spring Data JPA + Hibernate |
| `spring-boot-starter-data-redis` | Spring Data Redis + Lettuce |
| `spring-boot-starter-security` | Spring Security |
| `mybatis-plus-boot-starter` | MyBatis-Plus（第三方） |

加一行依赖就把一整套搭好。

---

## 6. 启动产物

```
target/
└── my-app-1.0.0.jar          ← 可直接 java -jar 跑（内嵌 Tomcat！）
```

```bash
mvn package
java -jar target/my-app-1.0.0.jar
```

不需要外置 Tomcat，**部署变得极其简单**。

---

## 7. DevTools 热重载（开发利器）

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
</dependency>
```

改了代码 → Build → 应用自动重启（毫秒级）。

> IDEA 默认要 `Ctrl+F9` (Mac `⌘+F9`) 手动 build，或在 Preferences 里开 "Build project automatically"。

---

## 8. 健康检查 Actuator

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: '*'
```

访问：
- `http://localhost:8080/actuator/health` → 服务健康状况
- `http://localhost:8080/actuator/info` → 应用元信息
- `http://localhost:8080/actuator/metrics` → 性能指标

---

## 9. 自查

- [ ] 在 Spring Initializr 上生成一个项目，跑起来访问 `/hello` 返回 200
- [ ] 解释 `@SpringBootApplication` 等价于哪三个注解
- [ ] 用一句话说明自动装配是怎么工作的
- [ ] 用 `mvn package` 打 jar，然后 `java -jar` 跑起来
- [ ] 加入 DevTools，改一行代码看到自动重启
- [ ] 加入 Actuator，访问 `/actuator/health`

## 代码示例

→ [`code/week6/hello-boot/`](../../code/week6/hello-boot/)
