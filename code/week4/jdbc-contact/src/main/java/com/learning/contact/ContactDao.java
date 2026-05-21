package com.learning.contact;

// DataSource 是 JDBC 提供数据库连接的标准接口
// 由具体实现（这里是 HikariCP）提供连接池能力
import javax.sql.DataSource;
// java.sql 是 JDBC 核心包：Connection / PreparedStatement / ResultSet 等
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 数据访问层（DAO = Data Access Object）：
 * 业务代码不直接接触 SQL，统一通过 DAO 访问数据库。
 *
 * 这是 MyBatis-Plus 普及前的"手写 JDBC" 模式，理解它有助于知道 ORM 在帮你省什么。
 */
public class ContactDao {

    // final 字段：构造时注入连接池，DAO 生命周期内不变
    // "依赖注入"思想：DAO 不自己创建 DataSource，从外面接收（方便测试时换 mock）
    private final DataSource ds;

    public ContactDao(DataSource ds) { this.ds = ds; }

    /** 插入并返回自增 id（如果数据库支持）*/
    public Long insert(Contact c) throws SQLException {
        // ? 是 SQL 占位符，PreparedStatement 会自动处理转义防 SQL 注入
        String sql = "INSERT INTO contact (name, phone, email) VALUES (?, ?, ?)";

        // try-with-resources 同时声明多个资源：Connection 和 PreparedStatement
        // 出 try 块时按声明的**反向顺序**自动 close（先 ps 后 conn）
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 sql,
                 // Statement.RETURN_GENERATED_KEYS: 告诉驱动"我要拿自增主键"
                 Statement.RETURN_GENERATED_KEYS)) {

            // 设置 SQL 参数：索引**从 1 开始**（不是 0！）
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());

            // executeUpdate 返回影响行数；这里通常是 1
            ps.executeUpdate();

            // 取自增主键（ResultSet 也是资源，try-with-resources）
            try (ResultSet keys = ps.getGeneratedKeys()) {
                // next() 把游标往后挪一行；有就 true
                if (keys.next()) {
                    long id = keys.getLong(1);     // 第 1 列是自增 id
                    c.setId(id);                    // 回填到对象
                    return id;
                }
            }
        }
        // 进不来这分支但 Java 要求 return
        return null;
    }

    /** 查全部 */
    public List<Contact> findAll() throws SQLException {
        String sql = "SELECT id, name, phone, email, created_at FROM contact ORDER BY id";

        // 准备返回的列表
        List<Contact> result = new ArrayList<>();

        // 三个资源一起声明
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // ResultSet 游标初始指向"第一行之前"，next() 移动到下一行
            // 没有更多行时返回 false
            while (rs.next()) {
                // 每行映射成一个 Contact 对象
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    /** 按 id 查；找不到返回 Optional.empty() */
    public Optional<Contact> findById(long id) throws SQLException {
        String sql = "SELECT id, name, phone, email, created_at FROM contact WHERE id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                // 三元判断：next() 有就包成 Optional 返回，无就 empty
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    /** 按姓名关键字模糊查 */
    public List<Contact> findByName(String namePattern) throws SQLException {
        String sql = "SELECT id, name, phone, email, created_at FROM contact WHERE name LIKE ? ORDER BY id";
        List<Contact> result = new ArrayList<>();
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // SQL LIKE 的 % 是通配符，% 之间的部分会被匹配
            ps.setString(1, "%" + namePattern + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    /** 更新；返回是否真的改了 */
    public boolean update(Contact c) throws SQLException {
        String sql = "UPDATE contact SET name = ?, phone = ?, email = ? WHERE id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setLong(4, c.getId());

            // executeUpdate 返回影响行数，> 0 说明改到了
            return ps.executeUpdate() > 0;
        }
    }

    /** 删除 */
    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM contact WHERE id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 把 ResultSet 当前行映射成 Contact 对象。
     * 抽出来复用，避免每个 select 方法都重复写一遍。
     */
    private Contact mapRow(ResultSet rs) throws SQLException {
        Contact c = new Contact();

        // 按列名取值（也可以用列索引，但名字更稳定）
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));

        // datetime → LocalDateTime
        // Timestamp 是 JDBC 提供的类型，可能为 null，要判一下
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) c.setCreatedAt(ts.toLocalDateTime());

        return c;
    }
}
