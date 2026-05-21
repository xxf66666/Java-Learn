# Week 7 · MyBatis-Plus + 持久层

> 目标：用 MyBatis-Plus 把数据库 CRUD 写得最快，落地一个"博客后端"：用户 / 文章 / 评论。

## 笔记顺序

| 序号 | 文件 | 主题 |
|------|------|------|
| 00 | [`00_mybatis_plus.md`](00_mybatis_plus.md) | MyBatis-Plus 起步、Entity 注解、Mapper、Service 通用 CRUD |
| 01 | [`01_query_wrapper.md`](01_query_wrapper.md) | QueryWrapper / LambdaQueryWrapper、分页、逻辑删除、自动填充 |
| 02 | [`02_transactional_in_boot.md`](02_transactional_in_boot.md) | 在 Spring Boot 项目里实战事务 |

## 配套代码

→ [`../../code/week7/`](../../code/week7/)
→ SQL: [`../../scripts/blog.sql`](../../scripts/blog.sql)

## 本周里程碑

- 在 MySQL 里设计 3 张表（user / article / comment）
- 用 MyBatis-Plus 写出所有 CRUD（90% 不写 SQL）
- 用 `LambdaQueryWrapper` 做复杂查询
- 加分页插件、逻辑删除、自动填充
- 完成博客后端：发文 / 评论 / 列文章 / 详情 / 删评论
