# Week 4 §02 · MySQL + JDBC

> 目标：在 MySQL 里建表、用 JDBC 操作数据库。

---

## 1. 启动 MySQL（Docker 一行）

```bash
docker run -d --name mysql8 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=learning \
  -p 3306:3306 \
  mysql:8

# 连进去
docker exec -it mysql8 mysql -uroot -proot
```

或直接装 MySQL Workbench / DBeaver / IDEA 自带的 Database 工具。

---

## 2. SQL 基础（5 分钟版）

```sql
-- 建库
CREATE DATABASE learning CHARACTER SET utf8mb4;
USE learning;

-- 建表
CREATE TABLE contact (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(64) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(128),
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_phone (phone)
);

-- 增
INSERT INTO contact (name, phone, email) VALUES ('Alice', '13800138000', 'a@x.com');

-- 查
SELECT * FROM contact WHERE name LIKE 'A%' ORDER BY created_at DESC LIMIT 10;

-- 改
UPDATE contact SET phone = '13900139000' WHERE id = 1;

-- 删
DELETE FROM contact WHERE id = 1;
```

### 常用约束

| 约束 | 含义 |
|------|------|
| `PRIMARY KEY` | 主键，唯一 + 非空 |
| `AUTO_INCREMENT` | 自增 |
| `NOT NULL` | 不允许 null |
| `UNIQUE KEY` | 唯一索引 |
| `DEFAULT` | 默认值 |
| `INDEX idx_xxx (col)` | 普通索引（加速查询） |

### 事务

```sql
START TRANSACTION;
UPDATE account SET balance = balance - 100 WHERE id = 1;
UPDATE account SET balance = balance + 100 WHERE id = 2;
COMMIT;          -- 或 ROLLBACK 撤销
```

**ACID**
- **A**tomicity 原子性：要么全成功要么全失败
- **C**onsistency 一致性
- **I**solation 隔离性
- **D**urability 持久性

---

## 3. JDBC：Java 操作数据库的标准 API

### 3.1 引入 MySQL 驱动

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.4.0</version>
</dependency>
```

### 3.2 最简单的 JDBC

```java
import java.sql.*;

String url = "jdbc:mysql://localhost:3306/learning?serverTimezone=Asia/Shanghai";
String user = "root";
String pwd = "root";

try (Connection conn = DriverManager.getConnection(url, user, pwd);
     PreparedStatement ps = conn.prepareStatement(
         "SELECT id, name, phone FROM contact WHERE name LIKE ?")) {
    ps.setString(1, "A%");
    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            String phone = rs.getString("phone");
            System.out.println(id + " " + name + " " + phone);
        }
    }
}
```

**关键**
- **永远用 `PreparedStatement`**（防 SQL 注入；性能更好）
- **永远 try-with-resources**（自动关 Connection / Statement / ResultSet）
- 参数索引**从 1 开始**（不是 0！）

### 3.3 增删改 → `executeUpdate()`

```java
try (PreparedStatement ps = conn.prepareStatement(
        "INSERT INTO contact (name, phone) VALUES (?, ?)",
        Statement.RETURN_GENERATED_KEYS)) {
    ps.setString(1, "Bob");
    ps.setString(2, "13900000000");
    int rows = ps.executeUpdate();              // 影响行数

    // 拿到自增 ID
    try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) {
            long newId = keys.getLong(1);
            System.out.println("新 ID = " + newId);
        }
    }
}
```

### 3.4 事务

```java
conn.setAutoCommit(false);             // 关闭自动提交
try {
    // 多条 SQL
    conn.commit();
} catch (Exception e) {
    conn.rollback();
    throw e;
}
```

---

## 4. 连接池：HikariCP

为什么需要：每次 `getConnection()` 都建 TCP 连接慢（几十 ms）。连接池**预先建好、循环使用**。

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.1.0</version>
</dependency>
```

```java
HikariConfig cfg = new HikariConfig();
cfg.setJdbcUrl("jdbc:mysql://localhost:3306/learning");
cfg.setUsername("root");
cfg.setPassword("root");
cfg.setMaximumPoolSize(10);

HikariDataSource ds = new HikariDataSource(cfg);

// 使用：每次 getConnection 极快（从池里拿）
try (Connection conn = ds.getConnection()) { ... }

// 应用关闭时
ds.close();
```

Spring Boot 默认就用 HikariCP，配置只要写 `application.yml`，自动接管。

---

## 5. DAO 模式：把 SQL 集中在一处

```java
public class ContactDao {
    private final DataSource ds;
    public ContactDao(DataSource ds) { this.ds = ds; }

    public Long insert(Contact c) throws SQLException {
        String sql = "INSERT INTO contact (name, phone, email) VALUES (?, ?, ?)";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : null;
            }
        }
    }

    public List<Contact> findAll() throws SQLException { ... }
    public Optional<Contact> findById(long id) throws SQLException { ... }
    public boolean update(Contact c) throws SQLException { ... }
    public boolean delete(long id) throws SQLException { ... }
}
```

Week 7 学 MyBatis-Plus 后，这些 SQL 都不用手写了，但**这套思想**贯穿始终：业务代码不直接接触 SQL，通过 DAO / Mapper 间接访问。

---

## 6. 自查

- [ ] Docker 起一个 MySQL，能通过 DBeaver 连上
- [ ] 跑通本周的 SQL 脚本（建库 / 建表 / 增删改查）
- [ ] 用 JDBC 写一段查询代码，能输出 5 条数据
- [ ] 解释为什么必须用 `PreparedStatement`（SQL 注入 + 性能）
- [ ] 用 HikariCP 替换 `DriverManager`
- [ ] 完成"通讯录 CLI"（见配套代码）

## 代码示例

→ [`code/week4/jdbc-contact/`](../../code/week4/jdbc-contact/)
→ SQL 脚本：[`scripts/contact.sql`](../../scripts/contact.sql)
