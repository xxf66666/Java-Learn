# Week 6 · 代码

> 配套笔记：[`../../docs/week6/`](../../docs/week6/)

## 模块清单

| 子目录 | 主题 | 跑法 |
|--------|------|------|
| `hello-boot/` | 最小 Spring Boot 应用 + Actuator + DevTools | `mvn spring-boot:run` 后访问 `/hello` |
| `rest-api/` | 商品 CRUD：Result 封装 + GlobalExceptionHandler + @Valid | `mvn spring-boot:run` 后用 `api.http` 调试 |
| `config-demo/` | 多环境 profile + `@ConfigurationProperties` | `mvn spring-boot:run` 默认 dev；`--spring.profiles.active=prod` 切换 |

## 跑 rest-api（推荐用 IDEA HTTP Client）

```bash
cd code/week6/rest-api
mvn spring-boot:run
# 浏览器 / Apifox / 直接打开 api.http 点 ▶
```

## 跑 config-demo 看 profile 切换

```bash
cd code/week6/config-demo
mvn spring-boot:run
# 访问 http://localhost:8080/info 看 mailHost = smtp.dev.local

# 切到 prod
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
# 访问 http://localhost:80/info 看 mailHost = smtp.prod.example.com
```

## 本周自查

- [ ] hello-boot 跑通，浏览器访问 `/hello` 看到响应
- [ ] rest-api 全部 5 个接口调通（GET / POST / PUT / DELETE / 搜索）
- [ ] 故意传错参数（name 空、price 负数），看 `@Valid` 校验失败响应
- [ ] config-demo 切换 dev / prod，看 `/info` 输出不同
- [ ] 把 `application.yml` 改个端口、重启验证生效
