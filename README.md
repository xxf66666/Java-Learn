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

> **关于本项目**：这是一份**个人学习记录**，不是教学权威材料。我有 Python 基础、接触过一点 C++，第一次系统学 Java，目标是做企业级 ERP 后端开发。仓库同步记录笔记、代码、踩坑、Demo —— 欢迎 issue / PR 批评指正。

---

## 项目目标

按 "**Python 视角入门 → 面向对象 + JVM → 持久层 → Web → Spring 全家桶 → ERP 实战**" 的节奏，把 Java 从语言层一路推到企业级应用。每周产出：**理论笔记 + 可运行代码 + 一次自我验收**。最终落地一个**多模块 Spring Boot ERP 系统**（用户/权限/物料/库存/采购/销售）。

## 进度概览

<table>
<tr>
<td width="50%" valign="top">

### Phase 1 · Java 语言基础 (Week 1-3) ✅

**任务**：从 Python 视角速通 Java 语法、面向对象、集合、并发、JVM

- Week 1：语法速通 + IDEA 工具链
- Week 2：OOP + 集合 + 异常 + IO（学生管理系统）
- Week 3：并发 + JVM + Lambda + Stream（多线程文件统计）

[详见 `docs/week1/` ~ `docs/week3/`](docs/)

</td>
<td width="50%" valign="top">

### Phase 2 · 工程化 + 持久层 (Week 4-5) ✅

**任务**：Maven、JUnit、SLF4J、JDBC、MySQL、第一份 Spring 入门

- Week 4：Maven 多模块 + JUnit 5 + Logback + JDBC 通讯录 CLI
- Week 5：Spring Core —— IoC / AOP / 事务（基于纯 spring-context）

[详见 `docs/week4/` ~ `docs/week5/`](docs/)

</td>
</tr>
<tr>
<td width="50%" valign="top">

### Phase 3 · Spring Boot Web 全栈 (Week 6-8) ✅

**任务**：Spring MVC + Spring Boot + MyBatis-Plus + Security + JWT

- Week 6：Spring Boot 起步、自动装配、配置管理、Spring MVC + 商品 CRUD
- Week 7：MyBatis-Plus（分页、自动填充、逻辑删除、乐观锁）+ 博客后端
- Week 8：Spring Security + JWT 鉴权 + Knife4j 文档

[详见 `docs/week6/` ~ `docs/week8/`](docs/)

</td>
<td width="50%" valign="top">

### Phase 4 · 中间件 + ERP 实战 (Week 9-12) ✅

**任务**：缓存、MQ、定时任务，最后落地完整 ERP 项目

- Week 9：Redis 缓存、RabbitMQ、Spring Task、文件上传
- Week 10：ERP 多模块工程 + RBAC 5 表 + 字典 + 操作日志
- Week 11：物料 / 仓库 / 库存 / 采购 / 销售 完整业务闭环
- Week 12：EasyExcel + Dashboard + `docker-compose` 一键部署

[详见 `docs/week9/` ~ `docs/week12/`](docs/)

</td>
</tr>
</table>

## 一键启 ERP（Week 10-12 产出）

```bash
git clone https://github.com/xxf66666/Java-Learn.git
cd Java-Learn/docker
docker-compose up -d --build
# 等几分钟构建完成
open http://localhost:8080/doc.html
# 登录：admin / admin123
```

完整业务流程演示（IDEA HTTP Client 打开 `code/project/api.http` 点 ▶）：

1. 登录 → 拿 JWT → 看我的菜单 / 权限
2. 创建采购单 → 审核 → 入库（库存 +10、流水 +1 条）
3. 创建销售单 → 审核 → 出库（库存 -3、流水 +1 条）
4. 查库存：剩 7；查流水：2 条；操作日志：自动入库

详细见 [`code/project/README.md`](code/project/README.md)。

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

## 给有 Python 基础的同学：速通建议

- **能快速带过的部分**：`if/for/while`、基本类型、函数调用、注释 —— Java 写法和 Python 区别不大，看一遍语法对照表就行
- **新概念，必须重点学**：静态强类型（变量先声明类型）、JVM（Java 的"解释器"）、引用 vs 值、泛型、注解、反射、Stream API、Spring 的 IoC/AOP 思想
- **Python 没有、但 Java 企业开发离不开的**：Maven 依赖管理、ORM 思维、AOP 切面、事务传播、Bean 生命周期
- **容易踩的坑**：Java 必须显式声明类型；判断字符串相等要用 `.equals()` 不能用 `==`；`String` 不可变（拼接用 `StringBuilder`）；没有多继承（用 interface）

详见 [`docs/week1/00_java_from_python.md`](docs/week1/00_java_from_python.md)。

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
