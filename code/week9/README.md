# Week 9 · 代码

> 配套笔记：[`../../docs/week9/`](../../docs/week9/)

## 模块清单

| 子目录 | 主题 | 跑前依赖 |
|--------|------|---------|
| `redis-cache/` | Redis 缓存 + Spring Cache `@Cacheable` | Redis 在 `:6379` |
| `rabbitmq-demo/` | RabbitMQ 发 / 收消息 | RabbitMQ 在 `:5672` |
| `scheduled-task/` | Spring Task `@Scheduled` | 无 |
| `file-upload/` | 文件上传 / 下载 | 无 |

## 启动中间件

```bash
docker run -d --name redis7 -p 6379:6379 redis:7

docker run -d --name rabbit \
  -p 5672:5672 -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=admin \
  -e RABBITMQ_DEFAULT_PASS=admin \
  rabbitmq:3-management
```

## 跑

```bash
cd code/week9/redis-cache && mvn spring-boot:run
# 访问 GET /api/product/1 两次，看耗时和 dbHitTotal 变化

cd code/week9/rabbitmq-demo && mvn spring-boot:run
# POST /api/send 一个 json，控制台看消费者收到
# 也可登 http://localhost:15672 观察 queue

cd code/week9/scheduled-task && mvn spring-boot:run
# 控制台每 10 秒打一条

cd code/week9/file-upload && mvn spring-boot:run
# 用 curl 测试：
# curl -F file=@README.md http://localhost:8082/api/file/upload
```

## 本周自查

- [ ] redis-cache：访问商品接口两次，第二次明显快（命中缓存）
- [ ] 修改商品调 `@CacheEvict`，再查商品看到新值
- [ ] rabbitmq-demo：发消息后控制台打印"收到消息"
- [ ] scheduled-task 每 10 秒打日志稳定
- [ ] file-upload 上传一个文件再下载回来，文件内容一致
