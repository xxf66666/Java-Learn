# Week 8 · 代码

> 配套笔记：[`../../docs/week8/`](../../docs/week8/)
> SQL：[`../../scripts/auth.sql`](../../scripts/auth.sql)

## 模块

| 子目录 | 主题 |
|--------|------|
| `auth-demo/` | 注册 / 登录 / JWT 鉴权 / 角色权限 / 全局异常 / 参数校验 / Knife4j 文档 |

## 跑起来

```bash
docker run -d --name mysql8 -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 mysql:8
mysql -h127.0.0.1 -uroot -proot < scripts/auth.sql

cd code/week8/auth-demo
mvn spring-boot:run

# 注册 / 登录 / 拿 token / 访问受保护接口
# 用 api.http 在 IDEA 里点 ▶
# 或访问 http://localhost:8080/doc.html 看 Knife4j 文档
```

## 本周自查

- [ ] 注册一个用户，密码在数据库里是 BCrypt 加密的（不是明文）
- [ ] 登录后拿到 token，用它访问 `/api/me` 成功
- [ ] 不带 token 访问 `/api/me` 返回 401
- [ ] 改 token 中一个字符再访问，返回 401
- [ ] 注册 ADMIN 角色（手动改 `register` 调用或直接改数据库 role 字段），用其 token 访问 `/api/admin/secret`
- [ ] 普通 USER 访问 `/api/admin/secret` 返回 403
- [ ] 访问 `/doc.html`，能看到 Knife4j 文档，可以在线试接口
