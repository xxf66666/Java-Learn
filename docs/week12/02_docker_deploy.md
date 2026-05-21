# Week 12 §02 · Docker + Docker Compose 部署

> 一键拉起 MySQL + Redis + ERP 应用，达到"克隆下来就能跑"的水平。

---

## 1. Dockerfile（多阶段构建）

写在 `code/project/erp-admin/Dockerfile`：

```dockerfile
# ===== Stage 1: build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
COPY erp-common/pom.xml      ./erp-common/
COPY erp-framework/pom.xml   ./erp-framework/
COPY erp-system/pom.xml      ./erp-system/
COPY erp-business/pom.xml    ./erp-business/
COPY erp-admin/pom.xml       ./erp-admin/
RUN mvn -B -q dependency:go-offline

COPY . .
RUN mvn -B -q clean package -DskipTests

# ===== Stage 2: runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/erp-admin/target/erp-admin-*.jar app.jar
ENV JAVA_OPTS="-Xms512m -Xmx1g"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**多阶段构建优势**：最终镜像不含 Maven 和源码，只有 JRE + jar，几百 MB 而非几个 GB。

---

## 2. docker-compose.yml

放在仓库根目录 `docker/docker-compose.yml`：

```yaml
version: "3.8"

services:
  mysql:
    image: mysql:8
    container_name: erp-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: erp
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./init-sql:/docker-entrypoint-initdb.d
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-uroot", "-proot"]
      interval: 10s
      retries: 5

  redis:
    image: redis:7
    container_name: erp-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data

  app:
    build:
      context: ../code/project
      dockerfile: erp-admin/Dockerfile
    container_name: erp-app
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_started
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/erp?serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
      JWT_SECRET: production-secret-must-be-at-least-32-bytes-long!
    ports:
      - "8080:8080"

volumes:
  mysql-data:
  redis-data:
```

---

## 3. SQL 初始化

`docker/init-sql/` 目录下放 SQL 文件，按字母序执行：

```
docker/init-sql/
├── 01-erp.sql
└── 02-erp-business.sql
```

直接把 `scripts/erp.sql` 和 `scripts/erp-business.sql` 复制过来即可。

---

## 4. 一键启动

```bash
cd docker
docker-compose up -d

# 看日志
docker-compose logs -f app

# 测一下
curl http://localhost:8080/api/login -X POST -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 停掉
docker-compose down

# 完全清理（包括数据）
docker-compose down -v
```

---

## 5. 配置外置

Spring Boot 会自动读取**环境变量**（驼峰转下划线大写）覆盖 yml 配置：
- yml `spring.datasource.url` → 环境变量 `SPRING_DATASOURCE_URL`
- yml `jwt.secret` → 环境变量 `JWT_SECRET`

部署时无需改 jar，改 compose 里的 `environment` 即可。

---

## 6. 生产建议

- 别用 `root` 用户连 MySQL —— 建个 `erp` 用户只授 erp 库权限
- jwt.secret / 数据库密码用 docker secrets 或外部 KMS，**不要写在 compose 里**
- 加 nginx 做 HTTPS 终结
- 加 Prometheus + Grafana 监控（Spring Boot Actuator `/actuator/prometheus`）

---

## 7. 自查

- [ ] `docker-compose up -d` 能起 MySQL / Redis / App 三个服务
- [ ] App 启动后能登录、能调通业务接口
- [ ] `docker-compose down && docker-compose up -d` 数据不丢失（volume 生效）
- [ ] 改 compose 里的 `JWT_SECRET` 后重启，能让旧 token 失效

## 代码示例

→ [`docker/`](../../docker/)
