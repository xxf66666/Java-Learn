# Week 7 · 代码

> 配套笔记：[`../../docs/week7/`](../../docs/week7/)
> 数据库脚本：[`../../scripts/blog.sql`](../../scripts/blog.sql)

## 模块

| 子目录 | 主题 |
|--------|------|
| `blog-backend/` | 博客后端：MyBatis-Plus + 用户 / 文章 / 评论 + 事务 + 自动填充 + 分页 + 逻辑删除 + 乐观锁 |

## 跑起来

```bash
# 1. 启 MySQL
docker run -d --name mysql8 -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 mysql:8

# 2. 跑建表脚本
mysql -h127.0.0.1 -uroot -proot < scripts/blog.sql

# 3. 启动应用
cd code/week7/blog-backend
mvn spring-boot:run

# 4. 调试接口
#    用 IDEA 打开 api.http 点 ▶ 逐条跑
#    或 Apifox / Postman
```

## 本周自查

- [ ] 发文 + 详情 + 列表 + 删除全套接口通
- [ ] 看控制台打印的 SQL（log-impl: StdOutImpl）
- [ ] 删除一篇文章后再查列表：看不到（逻辑删除生效）；但表里 deleted=1 还在
- [ ] 故意在 `ArticleService.publish` 的 `userMapper.updateById` 之后抛 RuntimeException，验证 article 没插入（事务回滚）
- [ ] 改一篇文章 → 看 `updated_at` 自动变化
- [ ] 两个并发请求改同一个 user（乐观锁示例），看版本号变化
