# Week 7 §00 · MyBatis-Plus 起步

> MyBatis-Plus (MP) 是国产、活跃、文档最好的 ORM 框架。继承 `BaseMapper<T>` 就免费送 18 个 CRUD 方法。

---

## 1. 引入依赖

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.7</version>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```

---

## 2. application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog?serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: root

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true     # 数据库 user_name 自动映射到 Java userName
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl   # 控制台打印 SQL（开发用）
  global-config:
    db-config:
      logic-delete-field: deleted          # 全局逻辑删除字段
      logic-delete-value: 1
      logic-not-delete-value: 0
```

---

## 3. Entity 实体

```java
@TableName("user")                          // 对应表名
public class User {
    @TableId(type = IdType.AUTO)             // 主键策略：自增
    private Long id;

    private String name;                     // 默认 name -> name
    private String email;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;          // 创建时自动填充

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;          // 创建 + 更新都自动填充

    @TableLogic                              // 逻辑删除字段
    private Integer deleted;

    // getter / setter
}
```

**主键策略 `IdType`**

| 值 | 含义 |
|----|------|
| `AUTO` | 数据库自增 |
| `ASSIGN_ID` | 雪花算法生成 Long ID（推荐 ERP 用） |
| `ASSIGN_UUID` | UUID |
| `INPUT` | 自己传 |

---

## 4. Mapper

继承 `BaseMapper<T>`，**什么都不用写**：

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

主类上加 `@MapperScan`：

```java
@SpringBootApplication
@MapperScan("com.learning.mapper")
public class Application { ... }
```

---

## 5. 18 个免费方法

```java
@Autowired
UserMapper userMapper;

// 增
User u = new User("Alice", "a@x.com");
userMapper.insert(u);          // 自增 ID 自动回填

// 删
userMapper.deleteById(1L);
userMapper.deleteByMap(Map.of("name", "Alice"));
userMapper.deleteBatchIds(List.of(1L, 2L, 3L));

// 改
u.setName("Alice 2");
userMapper.updateById(u);

// 查
User u1 = userMapper.selectById(1L);
List<User> users = userMapper.selectBatchIds(List.of(1L, 2L));
List<User> all = userMapper.selectList(null);    // 全部
Long count = userMapper.selectCount(null);
```

---

## 6. IService + ServiceImpl（推荐用法）

直接调 Mapper 也行，但通常封装一层 Service：

```java
public interface UserService extends IService<User> {
}

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
```

`IService` 提供了更丰富的方法：

```java
userService.save(u);                    // 同 insert
userService.saveBatch(list);            // 批量插入
userService.updateById(u);
userService.removeById(1L);
userService.getById(1L);
userService.list();                     // 全部
userService.list(wrapper);              // 带条件
userService.count();
userService.page(new Page<>(1, 10));    // 分页
```

---

## 7. 控制台看 SQL

开发期一定打开 `log-impl: StdOutImpl`，会打印每一条执行的 SQL + 参数 + 耗时。**调 bug 神器**。

生产环境关掉，或换成 `Slf4jImpl`（走日志框架）。

---

## 8. Code Generator（可选）

MP 提供代码生成器，根据数据库表自动生成 Entity / Mapper / Service / Controller：

```java
FastAutoGenerator.create(url, user, pwd)
    .globalConfig(builder -> builder.author("xxf").outputDir("src/main/java"))
    .packageConfig(builder -> builder.parent("com.learning.blog"))
    .strategyConfig(builder -> builder.addInclude("user", "article", "comment"))
    .execute();
```

不强求会用，但 IDEA 装 `MyBatisX` 插件后右键表也能一键生成。

---

## 9. 自查

- [ ] 跑通本周博客项目的 user 表 CRUD（用 BaseMapper 5 个方法）
- [ ] 用 `IService` 替代直接调 Mapper
- [ ] 在控制台看到打印的 SQL
- [ ] 把 `@TableLogic` 加到 `deleted` 字段，删除后表里还在但查不到
- [ ] 实体类配合 `@TableField(fill=...)` + Handler 自动填充时间

## 代码示例

→ [`code/week7/blog-backend/`](../../code/week7/blog-backend/)
