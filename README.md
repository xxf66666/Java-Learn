<div align="center">

# Java-Learn

**从 Java 语法到 Spring 全家桶 — 12 周走完语法、Web、持久层、安全、最后落到 ERP 项目**

[![Java 21 LTS](https://img.shields.io/badge/Java-21%20LTS-007396?logo=openjdk&logoColor=white)](https://openjdk.org)
[![Spring Boot 3.x](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5-DD0031)](https://baomidou.com)
[![MySQL 8](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com)
[![IntelliJ IDEA](https://img.shields.io/badge/IDE-IntelliJ%20IDEA-000000?logo=intellijidea&logoColor=white)](https://www.jetbrains.com/idea/)

</div>

---

> **关于本项目**：这是一份**个人学习记录**，不是教学权威材料。我有 C++ / Python 基础，第一次系统学 Java，目标是做企业级 ERP 后端开发。仓库同步记录笔记、代码、踩坑、Demo —— 欢迎 issue / PR 批评指正。

---

## 项目目标

按 "**语法对照速通 → 面向对象 + JVM → 持久层 → Web → Spring 全家桶 → ERP 实战**" 的节奏，把 Java 从语言层一路推到企业级应用。每周产出：**理论笔记 + 可运行代码 + 一次自我验收**。最终落地一个**多模块 Spring Boot ERP 系统**（用户/权限/物料/库存/采购/销售）。

## 进度概览

<table>
<tr>
<td width="50%" valign="top">

### Phase 1 · Java 语言基础 (Week 1-3)

**任务**：从 C++/Python 视角速通 Java 语法、面向对象、集合、并发、JVM

- Week 1：语法对照速通 + IDEA 工具链
- Week 2：OOP 三大特性、泛型、集合框架、异常、IO/NIO
- Week 3：并发（Thread / 锁 / 线程池）、JVM 内存模型、Lambda + Stream

[详见 `docs/week1/` ~ `docs/week3/`](docs/)

</td>
<td width="50%" valign="top">

### Phase 2 · 工程化 + 持久层 (Week 4-5)

**任务**：Maven、JUnit、SLF4J、JDBC、MySQL、第一份 Spring 入门

- Week 4：Maven / Gradle、JUnit 5、日志、JDBC + MySQL
- Week 5：Spring Core —— IoC 容器、Bean 生命周期、AOP、声明式事务

[详见 `docs/week4/` ~ `docs/week5/`](docs/)

</td>
</tr>
<tr>
<td width="50%" valign="top">

### Phase 3 · Spring Boot Web 全栈 (Week 6-8)

**任务**：Spring MVC + Spring Boot + MyBatis-Plus + Security + JWT

- Week 6：Spring Boot 起步、自动装配、配置管理、Spring MVC
- Week 7：MyBatis-Plus / JPA、分页、事务、多数据源
- Week 8：RESTful API 规范、Spring Security + JWT、统一异常、参数校验

[详见 `docs/week6/` ~ `docs/week8/`](docs/)

</td>
<td width="50%" valign="top">

### Phase 4 · 中间件 + ERP 实战 (Week 9-12)

**任务**：缓存、MQ、定时任务，最后落地完整 ERP 项目

- Week 9：Redis 缓存、RabbitMQ、XXL-Job、文件上传
- Week 10：ERP 架构设计 —— RBAC 权限、组织架构、字典、审计日志
- Week 11：ERP 业务 —— 物料 / 库存 / 采购 / 销售 / 订单
- Week 12：整合 + 前后端联调（Vue3 / Element-Plus）+ Docker 部署

[详见 `docs/week9/` ~ `docs/week12/`](docs/)

</td>
</tr>
</table>

## 仓库结构

```
Java-Learn/
├── docs/             # 每周笔记（理论 + 对照 + 踩坑）
│   ├── 00_learning_plan.md
│   └── weekN/
├── code/             # 每周代码（IDEA 可直接打开）
│   ├── weekN/
│   └── project/      # 最终 ERP 项目（多模块 Maven）
├── assets/           # 截图、架构图、UML
└── scripts/          # SQL 脚本、Docker compose、初始化脚本
```

## 学习节奏

- **每天 1-2 小时**：先看官方文档 / 视频，再敲代码进 `code/weekN/`，最后写笔记进 `docs/weekN/`
- **每周末验收**：跑通本周所有 demo，回顾笔记，把疑问标记成 `TODO` 留给下周
- **每阶段一个里程碑**：Phase 1 写一个命令行小工具；Phase 2 写一个 JDBC 通讯录；Phase 3 写一个 Spring Boot 博客；Phase 4 完成 ERP

## 环境清单

| 工具 | 版本 | 用途 |
|------|------|------|
| JDK | OpenJDK 21 LTS | 主开发 SDK |
| IntelliJ IDEA | 2024.3+ Ultimate（社区版也可，无 Spring 模板） | IDE |
| Maven | 3.9+ | 构建工具（IDEA 自带） |
| MySQL | 8.0 | 数据库 |
| Redis | 7.x | 缓存 |
| Docker Desktop | 最新 | 跑 MySQL / Redis / RabbitMQ |
| Postman / Apifox | 最新 | 接口测试 |
| Git | 最新 | 版本管理 |

具体安装与 IDEA 配置见 [`docs/week1/01_setup.md`](docs/week1/01_setup.md)。

## 给 C++/Python 老兵的速通建议

- **跳过的部分**：`if/for/while`、基本类型、函数调用、注释 —— 看一遍语法表就行
- **重点学的部分**：JVM 内存模型、类加载机制、引用类型 vs 值类型、泛型擦除、注解、反射、并发原语（`synchronized` / `volatile` / `CAS`）、Stream API、Spring 的 IoC/AOP 思想
- **企业开发独有的**：Maven 依赖管理、ORM 思维、AOP 切面、事务传播、Bean 生命周期 —— Python 写脚本基本不碰，但是 Java 企业开发的基本盘
- **少踩的坑**：Java 没有运算符重载、没有多继承（用 interface）、`==` 比较引用（用 `.equals()`）、`String` 不可变（拼接用 `StringBuilder`）

详见 [`docs/week1/00_java_for_cpp_python_dev.md`](docs/week1/00_java_for_cpp_python_dev.md)。

## 学习资源（精选）

**视频**
- [尚硅谷 Java 入门到精通（宋红康）](https://www.bilibili.com/video/BV1PY411e7J6) —— 基础语法
- [尚硅谷 Spring Boot 3 教程（雷丰阳）](https://www.bilibili.com/video/BV1Es4y1q7Bf) —— Spring 全家桶
- [黑马程序员 SpringCloud 微服务技术栈](https://www.bilibili.com/video/BV1LQ4y127n4) —— 进阶
- [程序员鱼皮 鱼书项目实战](https://www.codefather.cn/) —— 项目灵感

**书籍**
- 《Java 核心技术 卷I》第 12 版 —— Cay S. Horstmann
- 《Effective Java》第 3 版 —— Joshua Bloch（写完 Phase 1 再看，提升代码品味）
- 《深入理解 Java 虚拟机》第 3 版 —— 周志明（JVM 圣经，Phase 1 末看）
- 《Spring 实战》第 6 版 —— Craig Walls

**官方文档**
- [Java SE 21 Docs](https://docs.oracle.com/en/java/javase/21/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [MyBatis-Plus 文档](https://baomidou.com)

**开源 ERP 参考项目**
- [若依 ruoyi-vue-pro](https://gitee.com/zhijiantianya/ruoyi-vue-pro) —— 最经典的国产开源管理系统脚手架
- [pig-mesh/pig](https://gitee.com/log4j/pig) —— 微服务版
- [iBizPlatform / metaerp 等](https://gitee.com/explore/erp) —— 看一下行业实现

## License

MIT
