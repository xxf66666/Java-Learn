# Week 4 · Maven + JUnit + JDBC + MySQL

> 目标：从"手动 javac"升级到"用 Maven 管理项目"，能用 JUnit 写测试，能用 JDBC 操作 MySQL。

## 笔记顺序

| 序号 | 文件 | 主题 |
|------|------|------|
| 00 | [`00_maven.md`](00_maven.md) | Maven 是什么、`pom.xml` 怎么写、多模块项目 |
| 01 | [`01_junit_logback.md`](01_junit_logback.md) | JUnit 5 + Logback 日志 |
| 02 | [`02_mysql_jdbc.md`](02_mysql_jdbc.md) | MySQL 基础 + JDBC 模板 + HikariCP 连接池 |

## 配套代码

→ [`../../code/week4/`](../../code/week4/)

## 本周里程碑

到周末你应该能：
- 不查文档写一个 `pom.xml`，引入 3-5 个依赖
- 用 `mvn clean package` 打成 jar 包跑起来
- 写 5+ 个 JUnit 用例（包括参数化测试 + 异常断言）
- 在 MySQL 里建表、插数据、用 JDBC 查回来
- 完成"通讯录 CLI"：JDBC + MySQL 完整 CRUD
