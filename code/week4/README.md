# Week 4 · 代码

> 配套笔记：[`../../docs/week4/`](../../docs/week4/)

## 模块清单

| 子目录 | 主题 | 跑法 |
|--------|------|------|
| `maven-demo/` | 最小 Maven 项目 + commons-lang3 + 日志 + maven-shade 打可执行 jar | `mvn package; java -jar target/maven-demo-1.0.0-SNAPSHOT.jar Alice` |
| `junit-demo/` | JUnit 5 测试（基础 / 参数化 / 异常 / assertAll） | `mvn test` |
| `jdbc-contact/` | **综合**：JDBC + MySQL + HikariCP 通讯录 CLI | 见下方 |
| `multi-module/` | 多模块说明（自己练手） | —— |

## 跑 jdbc-contact

```bash
# 1. 启动 MySQL
docker run -d --name mysql8 -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 mysql:8

# 2. 跑建表 SQL（项目根目录）
mysql -h127.0.0.1 -uroot -proot < scripts/contact.sql

# 3. 跑应用
cd code/week4/jdbc-contact
mvn package
java -jar target/jdbc-contact-1.0.0-SNAPSHOT.jar
```

## 本周自查

- [ ] 用 IDEA 打开 maven-demo / junit-demo / jdbc-contact 三个 Maven 项目
- [ ] 跑 `mvn dependency:tree` 看依赖关系
- [ ] 在 IDEA 里跑测试（右键测试类 → Run），看到通过
- [ ] 改一行让测试失败，再修回来
- [ ] 跑通通讯录 CLI 的增删改查
