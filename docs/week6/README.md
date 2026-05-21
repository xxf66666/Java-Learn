# Week 6 · Spring Boot + Spring MVC

> 目标：从纯 Spring 升级到 Spring Boot，能写一个能跑、有 REST 接口的 Web 应用。

## 笔记顺序

| 序号 | 文件 | 主题 |
|------|------|------|
| 00 | [`00_spring_boot_intro.md`](00_spring_boot_intro.md) | Spring Boot 是什么、自动装配原理、Starter 机制 |
| 01 | [`01_spring_mvc.md`](01_spring_mvc.md) | Spring MVC：@RestController、@RequestMapping、参数绑定、Jackson |
| 02 | [`02_yaml_profile.md`](02_yaml_profile.md) | application.yml 配置 + 多环境 profile + @ConfigurationProperties |

## 配套代码

→ [`../../code/week6/`](../../code/week6/)

## 本周里程碑

到周末你应该能：
- 用 Spring Initializr 生成项目（或直接复制本周示例）
- 写出 GET / POST / PUT / DELETE 四个接口
- 用 Postman / Apifox / IDEA HTTP Client 调通
- 解释 `@SpringBootApplication` 等价于哪三个注解
- 配置 dev / prod 两套 profile 切换
- 用 `@ConfigurationProperties` 绑定一组配置
