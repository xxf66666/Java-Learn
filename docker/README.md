# Docker 部署

> 一键启 MySQL + Redis + ERP 后端。

## 启动

```bash
cd docker
docker-compose up -d --build
```

第一次构建大约 3-5 分钟（要下 Maven 依赖）。

## 验证

```bash
# 看应用日志
docker-compose logs -f app

# 等到看到 "Started AdminApplication"，访问：
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 或浏览器打开 http://localhost:8080/doc.html
```

## 停 / 清

```bash
docker-compose down        # 停容器，保留 volume
docker-compose down -v     # 全清（数据库数据也清）
```

## 文件清单

| 文件 | 作用 |
|------|------|
| `docker-compose.yml` | 三服务编排 |
| `init-sql/` | 容器首次启动自动跑的 SQL（建表 + 种子数据） |

## 端口

| 服务 | 容器端口 | 宿主端口 |
|------|---------|---------|
| MySQL | 3306 | 3306 |
| Redis | 6379 | 6379 |
| ERP App | 8080 | 8080 |

## 改配置

应用的配置走环境变量覆盖（见 `docker-compose.yml` 的 `environment`）：

- `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATA_REDIS_HOST` / `SPRING_DATA_REDIS_PORT`
- `JWT_SECRET` / `JWT_EXPIRE_DAYS`
- `SPRING_PROFILES_ACTIVE`

## 数据持久化

- MySQL 数据存在 `mysql-data` volume
- Redis 数据存在 `redis-data` volume

`docker-compose down`（不带 `-v`）不会清数据；重启后历史还在。
