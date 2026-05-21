# Week 7 §01 · QueryWrapper + 分页 + 字段填充

> 90% 的查询都能用 `LambdaQueryWrapper` 拼出来，不写一行 SQL。

---

## 1. QueryWrapper vs LambdaQueryWrapper

```java
// QueryWrapper：字段名用字符串（容易拼错且重构时 IDE 抓不到）
QueryWrapper<User> qw = new QueryWrapper<>();
qw.eq("name", "Alice").gt("age", 18);
userMapper.selectList(qw);

// LambdaQueryWrapper：字段名用方法引用（推荐 ✅）
LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
lqw.eq(User::getName, "Alice").gt(User::getAge, 18);
userMapper.selectList(lqw);
```

**永远用 Lambda 版**：字段名重构（`getName` → `getFullName`）IDEA 会一并改 wrapper 代码。

---

## 2. 常用方法

```java
new LambdaQueryWrapper<User>()
    .eq(User::getName, "Alice")                  // 等于
    .ne(User::getStatus, 0)                       // 不等于
    .gt(User::getAge, 18)                         // 大于
    .ge(User::getAge, 18)                         // ≥
    .lt(User::getAge, 60)                         // <
    .le(User::getAge, 60)                         // ≤
    .between(User::getAge, 18, 60)
    .like(User::getName, "Ali")                   // LIKE '%Ali%'
    .likeLeft(...)  // LIKE '%xxx'
    .likeRight(...) // LIKE 'xxx%'
    .in(User::getId, List.of(1, 2, 3))
    .notIn(...)
    .isNull(User::getDeletedAt)
    .isNotNull(...)
    .orderByDesc(User::getCreatedAt)
    .orderByAsc(User::getId)
    .select(User::getId, User::getName);           // 只查指定字段
```

### 动态条件（最实用）

```java
String name = req.getName();        // 可能为 null
Integer age = req.getMinAge();

new LambdaQueryWrapper<User>()
    .like(StringUtils.hasText(name), User::getName, name)
    .ge(age != null, User::getAge, age)
    .orderByDesc(User::getCreatedAt);

// 第一个 boolean 参数控制：true 才加这个条件
```

---

## 3. 分页

引入分页插件：

```java
@Configuration
@MapperScan("com.learning.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor i = new MybatisPlusInterceptor();
        i.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return i;
    }
}
```

使用：

```java
Page<User> page = new Page<>(1, 10);              // 第 1 页，10 条
Page<User> result = userMapper.selectPage(page,
    new LambdaQueryWrapper<User>().eq(User::getStatus, 1));

result.getRecords();        // 数据
result.getTotal();          // 总数
result.getPages();          // 总页数
result.getCurrent();        // 当前页
```

---

## 4. 自动填充

实体上：

```java
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createdAt;

@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updatedAt;
```

写 Handler：

```java
@Component
public class AutoFillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject mo) {
        strictInsertFill(mo, "createdAt", LocalDateTime.class, LocalDateTime.now());
        strictInsertFill(mo, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject mo) {
        strictUpdateFill(mo, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
```

之后 `insert` / `updateById` 时**不用手动设置时间字段**。

---

## 5. 逻辑删除

实体上：

```java
@TableLogic
private Integer deleted;        // 0 = 未删，1 = 已删
```

全局配置（application.yml 见 §00）。

之后：
- `deleteById(1L)` 实际执行 `UPDATE user SET deleted = 1 WHERE id = 1`
- 所有 `select` 自动加 `WHERE deleted = 0`

---

## 6. 乐观锁

并发更新场景常用：

```java
@Version
private Integer version;
```

加配置：

```java
i.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
```

更新时：

```java
User u = userMapper.selectById(1L);
u.setName("new");
userMapper.updateById(u);
// 实际 SQL: UPDATE user SET name = 'new', version = version + 1
//          WHERE id = 1 AND version = ?
// 如果有人抢先改过，更新失败（返回 0）
```

---

## 7. 写 XML 自定义 SQL（少数复杂场景）

复杂 join、统计、报表绕不开手写 SQL。在 `resources/mapper/` 下建 `UserMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.learning.mapper.UserMapper">

    <select id="selectActiveCountByDept" resultType="java.util.Map">
        SELECT dept, COUNT(*) AS cnt
        FROM user
        WHERE status = 1 AND deleted = 0
        GROUP BY dept
    </select>

</mapper>
```

```java
public interface UserMapper extends BaseMapper<User> {
    List<Map<String, Object>> selectActiveCountByDept();
}
```

```yaml
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
```

---

## 8. 自查

- [ ] 用 `LambdaQueryWrapper` 写一段"按名字模糊搜 + 按年龄过滤 + 按创建时间倒序"的查询
- [ ] 跑通分页查询，输出总数 / 总页 / 当前页
- [ ] 加 `@TableLogic`，删除后表里数据还在但查不到
- [ ] 实现 `MetaObjectHandler` 让 createdAt / updatedAt 自动填充
- [ ] 用 `@Version` 实现一次乐观锁更新

## 代码示例

→ [`code/week7/blog-backend/`](../../code/week7/blog-backend/)
