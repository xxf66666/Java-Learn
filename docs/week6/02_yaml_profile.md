# Week 6 §02 · application.yml + 多环境 Profile

> 配置文件是 Spring Boot 项目的"中央仪表盘"。学会用 yml + profile 切换 dev/prod。

---

## 1. `application.yml` 基础

放在 `src/main/resources/application.yml`：

```yaml
server:
  port: 8080
  servlet:
    context-path: /api          # 所有接口加 /api 前缀

spring:
  application:
    name: my-app
  datasource:
    url: jdbc:mysql://localhost:3306/learning
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai

logging:
  level:
    root: INFO
    com.learning: DEBUG
```

**为什么 yml 不用 properties**：层次清晰、看着舒服。生产里 80% 项目用 yml。

---

## 2. 多环境 profile

### 配置文件命名约定

```
src/main/resources/
├── application.yml             # 公共配置
├── application-dev.yml          # 开发环境覆盖
├── application-test.yml         # 测试环境
└── application-prod.yml         # 生产环境
```

### 主配置：选择激活的 profile

```yaml
# application.yml
spring:
  profiles:
    active: dev      # 默认用 dev
```

### 启动时切换

```bash
java -jar app.jar --spring.profiles.active=prod
# 或环境变量
SPRING_PROFILES_ACTIVE=prod java -jar app.jar
```

### 加载顺序

`application.yml` 先加载 → `application-{active}.yml` 覆盖。**子文件没写的字段，沿用主文件**。

---

## 3. `@Value` 读单个配置

```java
@Component
public class MailService {
    @Value("${mail.host}")
    private String host;

    @Value("${mail.port:25}")              // 默认值 25
    private int port;

    @Value("${mail.recipients}")            // 逗号分隔
    private List<String> recipients;
}
```

---

## 4. `@ConfigurationProperties` 批量绑定（推荐）

一组配置一起绑定到对象上：

```yaml
mail:
  host: smtp.example.com
  port: 587
  username: noreply@x.com
  password: secret
  recipients:
    - a@x.com
    - b@x.com
```

```java
@Component
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private List<String> recipients;
    // getter/setter（必须）
}
```

```java
@Service
public class MailService {
    private final MailProperties props;
    public MailService(MailProperties props) { this.props = props; }
}
```

**优势**
- 一个类管一组配置，比散落各处的 `@Value` 清爽
- 支持 IDE 提示（如果加了 `spring-boot-configuration-processor`）
- 支持嵌套 / 集合 / Map

---

## 5. 配置文件外置（部署时换配置不重新打包）

把 `application-prod.yml` 放在 jar 同目录：

```
deploy/
├── app.jar
└── application-prod.yml          ← 启动时 Spring Boot 会优先读这个
```

启动：
```bash
java -jar app.jar --spring.profiles.active=prod
```

加载优先级（高 → 低）：
1. 命令行 `--key=value`
2. JVM 系统属性 `-Dkey=value`
3. 环境变量
4. jar 同目录的 `application.yml` / `application-{profile}.yml`
5. classpath 的 `application.yml` / `application-{profile}.yml`

**生产实操**：敏感配置（数据库密码）走环境变量，业务配置走 yml。

---

## 6. 配置加密（不在密码里）

`spring-boot-starter-actuator` 自带 `/actuator/env`，会泄漏密码。

生产环境：
- 用 [Jasypt](https://github.com/ulisesbocchio/jasypt-spring-boot) 加密敏感配置
- 或走配置中心（Nacos、Apollo、Spring Cloud Config）

学习阶段先把密码放 `application-prod.yml` + `.gitignore`。

---

## 7. 自查

- [ ] 写 `application.yml` + `application-dev.yml` + `application-prod.yml`
- [ ] 用 `--spring.profiles.active=prod` 切换看日志变化
- [ ] 用 `@ConfigurationProperties` 绑定一组配置
- [ ] 用 `@Value("${xxx:default}")` 提供默认值
- [ ] 解释配置加载优先级（命令行 > 环境变量 > 外置文件 > 内置文件）

## 代码示例

→ [`code/week6/config-demo/`](../../code/week6/config-demo/)
