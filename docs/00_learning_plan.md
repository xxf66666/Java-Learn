# Java + Spring + ERP 学习计划

> 适合对象：有 C++ / Python 基础，首次系统学习 Java，目标是企业级 ERP 后端开发
> 总时长：12 周（每天 1-2 小时，周末多投入 2-3 小时）
> 终极产出：一个可运行的多模块 Spring Boot ERP 系统 + 完整笔记

---

## 目录结构

```
Java-Learn/
├── docs/         # 学习笔记，按周组织
├── code/         # 所有代码，按周组织，最终项目在 code/project/
├── assets/       # 架构图、截图、UML、ER 图
└── scripts/      # SQL 初始化脚本、Docker compose
```

---

## 路线总览（12 周 / 4 阶段）

| Phase | 周次 | 主题 | 关键产出 |
|-------|------|------|----------|
| **1. 语言基础** | W1 | Java 语法速通（C++/Python 对照） | 命令行计算器 |
| | W2 | OOP + 集合 + 异常 + IO | 学生管理系统（内存版） |
| | W3 | 并发 + JVM + Stream | 多线程文件统计工具 |
| **2. 工程化 + 持久层** | W4 | Maven + JUnit + JDBC + MySQL | JDBC 通讯录 |
| | W5 | Spring Core（IoC / AOP / 事务） | XML+注解版 Spring Demo |
| **3. Spring Boot 全栈** | W6 | Spring Boot + Spring MVC | 第一个 REST API |
| | W7 | MyBatis-Plus + 分页 + 事务 | 博客系统后端 |
| | W8 | Spring Security + JWT + 校验 + 异常 | 完整鉴权博客 |
| **4. 中间件 + ERP** | W9 | Redis + MQ + 定时任务 + 文件 | 中间件实验集 |
| | W10 | ERP 架构 + RBAC + 字典 + 审计 | ERP 基础模块 |
| | W11 | ERP 业务：物料 / 库存 / 采购 / 销售 | ERP 业务模块 |
| | W12 | 整合 + 前后端联调 + Docker 部署 | ERP 完整 Demo |

---

## Phase 1 · Java 语言基础（Week 1-3）

### Week 1：Java 语法速通 + IDEA 工具链

**核心概念**
- JDK / JRE / JVM 关系，class 文件如何运行
- 基本类型 vs 引用类型，`==` vs `.equals()`
- `String` 不可变、`StringBuilder` 拼接
- 控制流（与 C++/Python 几乎一致）、方法定义
- 包（package）、import、访问修饰符（public/private/protected/默认）

**与 C++/Python 对照重点**
- C++ 的指针 / 引用 → Java 全是引用，没有指针运算
- Python 的鸭子类型 → Java 静态强类型，必须声明类型
- C++ 的析构函数 → Java 没有，GC 处理；`try-with-resources` 替代 RAII
- Python 的列表推导式 → Java 用 Stream API 替代（W3 学）

**学习资源**
- [Oracle Java Tutorials - Getting Started](https://docs.oracle.com/javase/tutorial/getStarted/index.html)
- 尚硅谷宋红康 Java 入门（前 50 集，1.5 倍速）
- 《Java 核心技术 卷I》第 3-4 章

**实践任务（提交到 `code/week1/`）**
```
code/week1/
  ├── HelloWorld.java                # 第一个程序
  ├── SyntaxCheatsheet.java          # 语法对照速查（变量/控制流/数组/字符串）
  ├── CalculatorCli.java             # 命令行四则运算（Scanner 输入）
  └── README.md                      # 本周笔记入口
```

**验收标准**：能在 IDEA 里新建项目、运行、debug 断点；能用 Scanner 读输入、用 println 输出；能解释 `String s1 = "a"; String s2 = "a";` 中 `s1 == s2` 为什么是 true。

---

### Week 2：面向对象 + 集合 + 异常 + IO

**核心概念**
- 类 / 对象 / 构造器 / `this` / `super`
- 三大特性：封装、继承、多态（与 C++ 对照：Java 默认动态绑定）
- `abstract` 抽象类 vs `interface` 接口（Java 8+ default method）
- 泛型（类型擦除、`<T extends Comparable<T>>` 上界、`? super T` 下界）
- 集合框架：`ArrayList` / `LinkedList` / `HashMap` / `HashSet` / `TreeMap` / `LinkedHashMap`
- 异常体系：`Throwable` → `Error` / `Exception` → `RuntimeException`；checked vs unchecked
- IO / NIO：`File`、`FileInputStream`、`BufferedReader`、`Files.readAllLines`（推荐 NIO.2）

**与 C++/Python 对照重点**
- C++ STL `std::vector` → Java `ArrayList`；`std::map` → `HashMap`（无序）/ `TreeMap`（有序）
- Python `list` / `dict` 几乎对应 `ArrayList` / `HashMap`
- C++ 多继承 → Java 单继承 + 多接口
- Python 的 `with open()` → Java 的 `try-with-resources`

**学习资源**
- 尚硅谷宋红康 第 51-150 集（OOP + 集合）
- 《Java 核心技术 卷I》第 5-9 章
- [Baeldung - Guide to Java Generics](https://www.baeldung.com/java-generics)

**实践任务（`code/week2/`）**
```
code/week2/
  ├── shape/                       # 抽象类 + 多态：Shape / Circle / Rectangle
  ├── generic/                     # 自定义泛型容器 MyStack<T>
  ├── student/                     # 综合：StudentManager（用 HashMap 存）
  │   ├── Student.java
  │   ├── StudentManager.java      # 增删改查
  │   └── StudentManagerTest.java
  └── io/                          # 文件读写、CSV 解析
```

**验收标准**：能解释为什么 `List<String>` 和 `List<Integer>` 在运行时是同一个类；能在不查 API 的情况下写出 HashMap 的常用操作；能区分 checked / unchecked 异常并解释什么时候 throws 什么时候 catch。

---

### Week 3：并发 + JVM + Lambda + Stream

**核心概念**
- 线程：`Thread` / `Runnable` / `Callable` / `Future`
- 同步：`synchronized` / `volatile` / `ReentrantLock` / `AtomicInteger`
- 线程池：`ExecutorService` / `ThreadPoolExecutor`（四个参数 + 拒绝策略）
- JUC：`CountDownLatch` / `CyclicBarrier` / `Semaphore` / `ConcurrentHashMap`
- JVM 内存模型：栈 / 堆 / 方法区 / 元空间；GC 算法（G1 / ZGC 简介）
- 类加载机制：双亲委派（面试高频）
- Lambda 表达式、函数式接口（`Function` / `Predicate` / `Consumer` / `Supplier`）
- Stream API：`filter` / `map` / `reduce` / `collect` / `groupingBy`
- `Optional` 用法（避免 NPE）

**与 C++/Python 对照重点**
- C++ `std::thread` / Python `threading` → Java `Thread`，但 Java 推荐用线程池而非裸 Thread
- Python 的 GIL → Java 没有，是真并行；但要小心可见性 / 原子性
- Python 列表推导式 → Stream API（懒求值，可并行）

**学习资源**
- 尚硅谷宋红康 第 200-280 集（并发 + JVM）
- 《Java 并发编程实战》第 1-6 章（Brian Goetz，并发圣经）
- 《深入理解 JVM》第 2、3、7 章
- [Oracle Concurrency Tutorial](https://docs.oracle.com/javase/tutorial/essential/concurrency/)

**实践任务（`code/week3/`）**
```
code/week3/
  ├── thread_basics/               # Thread / Runnable / Callable 三种方式
  ├── thread_pool/                 # ExecutorService 实战
  ├── concurrent_map/              # ConcurrentHashMap vs synchronized HashMap 对比
  ├── stream_demo/                 # Stream 替代传统 for 循环
  └── file_counter/                # 综合：多线程统计目录下所有 .java 文件的代码行数
```

**验收标准**：能解释 `synchronized` 锁的是什么、`volatile` 解决了什么问题；能手写一个 ThreadPoolExecutor 四参构造；能用 Stream 实现 "按部门分组求平均薪资"；能画出 JVM 内存结构图。

---

## Phase 2 · 工程化 + 持久层（Week 4-5）

### Week 4：Maven + JUnit + 日志 + JDBC + MySQL

**核心概念**
- Maven：`pom.xml`、坐标（groupId / artifactId / version）、生命周期、依赖范围（compile/test/provided）
- 多模块 Maven 项目（为 ERP 铺垫）
- JUnit 5：`@Test` / `@BeforeEach` / `@ParameterizedTest` / Mockito 入门
- 日志门面：SLF4J + Logback，logback-spring.xml 配置
- JDBC：`DriverManager` / `Connection` / `PreparedStatement` / `ResultSet`、SQL 注入与防御
- 连接池：HikariCP（Spring Boot 默认）
- MySQL 基础：建表、索引、事务、ACID、隔离级别

**学习资源**
- [Maven 官方入门 5 分钟](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)
- 尚硅谷 Maven 教程
- [MySQL 8 官方文档](https://dev.mysql.com/doc/refman/8.0/en/)
- 《高性能 MySQL》第 4 版（参考，不必通读）

**实践任务（`code/week4/`）**
```
code/week4/
  ├── maven_demo/                  # Maven 多模块项目骨架（common / dao / service）
  ├── junit_demo/                  # JUnit 5 + Mockito 示例
  ├── jdbc_contact/                # 通讯录 CLI：JDBC + MySQL 增删改查
  │   ├── sql/init.sql
  │   ├── dao/ContactDao.java
  │   └── App.java
  └── logback_demo/                # SLF4J + Logback 配置示例
```

**验收标准**：能从零写出一个 Maven 多模块工程；能用 JDBC + PreparedStatement 安全地查 MySQL；能解释为什么要用 SLF4J 门面而不是直接用 Logback / Log4j。

---

### Week 5：Spring Core（IoC / AOP / 事务）

**核心概念**
- IoC（控制反转）/ DI（依赖注入）—— 为什么需要、解决了什么
- Bean 定义：XML / `@Component` / `@Configuration + @Bean`
- 依赖注入方式：构造器（推荐）/ Setter / 字段
- Bean 作用域：`singleton` / `prototype` / `request` / `session`
- Bean 生命周期（初始化 / 销毁回调）
- AOP（面向切面）：切面 / 切点 / 通知 / 织入；动态代理（JDK Proxy vs CGLib）
- 声明式事务：`@Transactional` 传播行为（REQUIRED / REQUIRES_NEW 等）
- 配置类（`@Configuration` + `@ComponentScan`）

**学习资源**
- [Spring Framework Reference - Core](https://docs.spring.io/spring-framework/reference/core.html)
- 尚硅谷雷丰阳 Spring 注解驱动开发
- 《Spring 实战》第 6 版 第 1-3 章

**实践任务（`code/week5/`）**
```
code/week5/
  ├── ioc_xml/                     # 纯 XML 配置 Bean（理解原理）
  ├── ioc_annotation/              # 注解版（实际工作用法）
  ├── aop_log/                     # AOP 自动打印方法入参/出参/耗时
  └── tx_demo/                     # 转账场景的事务传播实验
```

**验收标准**：能解释 IoC 容器解决的根本问题；能手写一个 AOP 切面给 Service 层加日志；能正确使用 `@Transactional` 并指出 "自调用失效" 的坑。

---

## Phase 3 · Spring Boot 全栈（Week 6-8）

### Week 6：Spring Boot 入门 + Spring MVC

**核心概念**
- Spring Boot 的自动装配原理（`@SpringBootApplication` 拆解）
- `application.yml` 配置 + 多环境（dev / test / prod）
- Spring MVC：`@RestController` / `@RequestMapping` / `@PathVariable` / `@RequestBody`
- Jackson 序列化（日期格式、null 处理、`@JsonIgnore`）
- 拦截器 vs 过滤器 vs AOP
- DevTools 热重载、Actuator 健康检查

**学习资源**
- [Spring Boot Reference Doc](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- 尚硅谷雷丰阳 Spring Boot 3 教程
- [Spring Initializr](https://start.spring.io) —— 项目生成器，IDEA 里直接集成

**实践任务（`code/week6/`）**
```
code/week6/
  ├── hello-boot/                  # 第一个 Spring Boot 应用
  ├── rest-api/                    # 商品 CRUD REST 接口，内存存储
  └── config-demo/                 # 多环境配置 + @ConfigurationProperties
```

**验收标准**：能用 IDEA 的 Spring Initializr 生成项目并跑起来；能写出 GET/POST/PUT/DELETE 四个接口；能解释 `@SpringBootApplication` 等价于哪三个注解。

---

### Week 7：MyBatis-Plus + 持久层

**核心概念**
- MyBatis 核心：`mapper.xml` + `Mapper` 接口绑定、动态 SQL（`<if>` / `<foreach>`）
- MyBatis-Plus：通用 CRUD（继承 `BaseMapper`）、条件构造器（`QueryWrapper` / `LambdaQueryWrapper`）
- 分页插件、逻辑删除、乐观锁、自动填充（createTime / updateTime）
- 事务：`@Transactional` 在 Spring Boot 中的正确用法
- 多数据源（为 ERP 准备）
- JPA / Hibernate 简介（与 MyBatis 对比，知道何时选哪个）

**学习资源**
- [MyBatis-Plus 官方文档](https://baomidou.com)
- [MyBatis 3 文档](https://mybatis.org/mybatis-3/zh_CN/)
- 黑马程序员 MyBatis-Plus 教程

**实践任务（`code/week7/`）**
```
code/week7/
  ├── blog-backend/                # 博客后端：用户 / 文章 / 评论
  │   ├── sql/blog.sql
  │   ├── entity/                  # User / Article / Comment
  │   ├── mapper/                  # 继承 BaseMapper
  │   ├── service/                 # IService + ServiceImpl
  │   ├── controller/              # REST 接口
  │   └── application.yml
  └── multi-datasource-demo/       # 主从数据源切换
```

**验收标准**：能不查文档写出 MyBatis-Plus 的分页查询；能解释 `@Transactional(propagation = REQUIRES_NEW)` 在嵌套调用中的行为；能配置打印执行的 SQL。

---

### Week 8：Spring Security + JWT + 工程规范

**核心概念**
- Spring Security 6 架构：`SecurityFilterChain` / `UserDetailsService` / `AuthenticationProvider`
- 认证 vs 授权；表单登录 vs JWT 无状态
- JWT 原理 + jjwt 库使用
- RBAC 模型：用户 / 角色 / 权限
- 统一响应封装（`Result<T>` / `Page<T>`）
- 全局异常处理（`@RestControllerAdvice`）
- 参数校验（`@Valid` + `@NotNull` / `@NotBlank` / 自定义校验注解）
- 接口文档：Knife4j / SpringDoc OpenAPI 3

**学习资源**
- [Spring Security 6 Reference](https://docs.spring.io/spring-security/reference/)
- 三更草堂 Spring Security 教程（B站搜索）
- [Knife4j 文档](https://doc.xiaominfo.com)

**实践任务（`code/week8/`）**
```
code/week8/
  ├── auth-demo/                   # 完整登录注册 + JWT 鉴权
  │   ├── security/                # SecurityConfig / JwtFilter
  │   ├── util/JwtUtil.java
  │   ├── exception/GlobalExceptionHandler.java
  │   └── result/Result.java
  └── validation-demo/             # 参数校验 + 自定义注解
```

**验收标准**：能从零搭建一个 JWT 鉴权流程；能解释 Filter 在 Spring Security 中的执行顺序；能用 `@RestControllerAdvice` 把所有异常包装成统一格式。

---

## Phase 4 · 中间件 + ERP 实战（Week 9-12）

### Week 9：Redis + MQ + 定时任务 + 文件

**核心概念**
- Redis 五大类型：String / Hash / List / Set / ZSet 各自适用场景
- Spring Data Redis（`RedisTemplate` / `StringRedisTemplate`）
- 缓存策略：Cache Aside、缓存穿透 / 击穿 / 雪崩及对策
- Spring Cache 注解（`@Cacheable` / `@CacheEvict`）
- RabbitMQ 基础：Exchange / Queue / Binding；direct / topic / fanout
- 业务场景：异步通知、订单超时取消、削峰
- XXL-Job / Spring Task 定时任务
- 文件上传（MultipartFile）+ 阿里云 OSS / MinIO

**学习资源**
- 黑马点评（瑞吉外卖姊妹篇）—— Redis 实战
- [Spring Data Redis Reference](https://docs.spring.io/spring-data/redis/reference/)
- [RabbitMQ Tutorials](https://www.rabbitmq.com/tutorials)
- [XXL-Job 文档](https://www.xuxueli.com/xxl-job/)

**实践任务（`code/week9/`）**
```
code/week9/
  ├── redis-cache/                 # Spring Cache + Redis，模拟商品缓存
  ├── rabbitmq-demo/               # 订单创建发消息 → 异步发短信
  ├── xxl-job-demo/                # 接入 XXL-Job 跑定时任务
  └── file-upload/                 # MinIO 文件上传 + 下载
```

**验收标准**：能解释 "为什么用 Redis 做缓存而不是 HashMap"；能写出一个用 Redis 解决缓存穿透 + 击穿 + 雪崩的方案；能用 RabbitMQ 实现订单 30 分钟未支付自动取消。

---

### Week 10：ERP 架构设计 + 基础模块

**ERP 系统是什么**：Enterprise Resource Planning，企业资源计划。核心模块（按行业不同有差异）：
- **基础**：用户管理、角色权限、组织架构、字典、参数配置、操作日志
- **采购**：供应商、采购订单、入库
- **销售**：客户、销售订单、出库、发票
- **库存**：物料档案、仓库、库存调拨、盘点
- **财务**：应收应付、记账凭证（深水区，本路线不深入）
- **生产**（MRP）：BOM、工单、排程（更深水区）

**本周聚焦：基础模块**
- 系统架构选型：单体 Spring Boot（够用）vs 微服务（Phase 4 之后再说）
- 项目模块拆分：`common` / `framework` / `system` / `business` / `admin`
- RBAC 5 表设计：`sys_user` / `sys_role` / `sys_menu` / `sys_user_role` / `sys_role_menu`
- 菜单 + 按钮级权限（前端配合）
- 数据权限（部门 / 自己 / 自定义范围）
- 字典管理（性别、状态、单据类型等）
- 操作日志（基于 AOP）

**学习资源**
- [若依 ruoyi-vue-pro 源码](https://gitee.com/zhijiantianya/ruoyi-vue-pro) —— 直接读源码！这是 ERP 学习的金矿
- [pig 微服务版](https://gitee.com/log4j/pig)
- 《企业应用架构模式》—— Martin Fowler（思想层，不必读完）

**实践任务（`code/project/`，从本周开始）**
```
code/project/
  ├── pom.xml                      # 父 pom，依赖版本管理
  ├── erp-common/                  # Result / 工具类 / 常量 / 枚举
  ├── erp-framework/               # Security / Redis / MyBatis-Plus 配置
  ├── erp-system/                  # 用户 / 角色 / 菜单 / 部门 / 字典 / 日志
  └── erp-admin/                   # 主启动模块（main）
  scripts/erp.sql                  # 完整建表脚本（含 RBAC 初始化数据）
```

**验收标准**：能独立设计 5 表 RBAC（用户 / 角色 / 菜单 / 用户-角色 / 角色-菜单）；能用 AOP 实现操作日志自动记录；能解释数据权限的 4 种范围（全部 / 本部门 / 本部门及以下 / 仅本人 / 自定义）。

---

### Week 11：ERP 业务模块

**聚焦：物料 / 库存 / 采购 / 销售**

- **物料档案**：物料编码（规则生成）、分类树、计量单位
- **仓库 / 库位**：多仓库管理，库位精细化
- **库存**：当前库存（`stock`）+ 库存流水（`stock_log`，每一次出入库都留痕）
- **采购流程**：采购订单 → 入库单 → 库存增加 + 流水记录 → 应付增加
- **销售流程**：销售订单 → 出库单 → 库存减少 + 流水记录 → 应收增加
- **单据通用模式**：表头（订单主表）+ 表体（订单明细，一对多）；单据状态机（待审核 → 已审核 → 已完成 / 已作废）
- **编码规则**：`PO20251234567`（前缀 + 日期 + 流水号）
- **核心难点**：并发下扣减库存（乐观锁 / 分布式锁 / 数据库行锁，选一个深入做）

**学习资源**
- 若依 ruoyi-vue-pro 的 ERP 模块源码 —— 直接抄结构，理解每个表的设计意图
- [畅捷通 / 用友 / 金蝶的产品文档（公开部分）](https://www.chanjet.com) —— 看真实 ERP 的字段长什么样
- B站搜索 "ERP 项目实战 Spring Boot"

**实践任务（接 `code/project/`）**
```
code/project/
  ├── erp-business/
  │   ├── material/                # 物料档案 + 分类
  │   ├── warehouse/               # 仓库 / 库位
  │   ├── stock/                   # 库存 + 流水
  │   ├── purchase/                # 采购订单 + 入库
  │   └── sale/                    # 销售订单 + 出库
  └── scripts/erp-business.sql
```

**验收标准**：能完整跑通 "下采购单 → 入库 → 库存增加 → 销售下单 → 出库 → 库存减少" 全流程；能在并发 100 个销售请求时不超卖（用 Redis 分布式锁或 MySQL 行锁均可，但要能解释原理）。

---

### Week 12：整合 + 前后端联调 + 部署

**任务**
- 前端：克隆若依 / iview-admin / vue-element-admin 模板，对接自己的后端
- 完善：导入导出（EasyExcel）、报表（简单的饼图/折线图 + ECharts）、Dashboard
- Docker 化：写 `Dockerfile` + `docker-compose.yml`（包含 MySQL / Redis / 应用）
- CI/CD（可选）：GitHub Actions 自动构建镜像
- 性能压测（可选）：JMeter / wrk
- 写完整 README + 部署文档

**学习资源**
- [EasyExcel 文档](https://easyexcel.opensource.alibaba.com)
- [vue-element-admin](https://github.com/PanJiaChen/vue-element-admin)
- [Docker 入门](https://docs.docker.com/get-started/)

**最终验收**
- [ ] 仓库 README 完整（架构图 + 模块说明 + 截图）
- [ ] 一键 `docker-compose up` 跑起来全栈
- [ ] 至少 10 张系统截图（登录 / 主页 / 用户管理 / 物料 / 采购下单 / 库存 / Dashboard）
- [ ] 单元测试覆盖核心 Service（至少 50%）
- [ ] Postman / Apifox 接口集合
- [ ] 一篇 "踩坑总结" 笔记

---

## 环境配置（一次性，Week 1 完成）

```bash
# 1. JDK 21（macOS 推荐 brew + sdkman）
brew install openjdk@21
# 或 https://sdkman.io 管理多版本

# 2. Maven（IDEA 自带，可不装；如需 CLI）
brew install maven

# 3. IDEA Ultimate（学生 / 试用 / 付费）
# https://www.jetbrains.com/idea/

# 4. MySQL 8 + Redis 7（推荐 Docker，免污染本机）
docker run -d --name mysql8 -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 mysql:8
docker run -d --name redis7 -p 6379:6379 redis:7

# 5. 数据库可视化：DBeaver（免费）或 Navicat
# 6. 接口测试：Apifox（推荐，国产，免费）或 Postman
```

详细步骤见 [`docs/week1/01_setup.md`](week1/01_setup.md)。

---

## 推荐学习节奏

```
官方文档(标准答案)
    → 尚硅谷视频(直觉)
    → 自己敲代码进 code/weekN/
    → 写笔记进 docs/weekN/
    → 每周末跑通所有 demo + 把疑问列成 TODO
    → 阶段末写一个小项目串起来
```

**避免的陷阱**
1. ❌ 只看视频不写代码 —— Java 的坑必须在 IDEA 里踩
2. ❌ 一上来学 Spring —— 没有 Java 基础学 Spring 是空中楼阁
3. ❌ 追求一次学全 —— Java / Spring 生态极大，按用到的学，回头补
4. ❌ 拒绝看英文文档 —— 官方文档是标准答案，中文教程都是二手信息
5. ❌ 忽视测试 —— 工业代码必须有单测，越早养成习惯越好
