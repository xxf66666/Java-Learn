# Week 9 · Redis + 消息队列 + 定时任务 + 文件

> 目标：掌握 ERP 项目要用的几个中间件 / 组件，每个学到"会用 + 知道坑"。

## 笔记顺序

| 序号 | 文件 | 主题 |
|------|------|------|
| 00 | [`00_redis.md`](00_redis.md) | Redis 五大类型 + Spring Data Redis + Spring Cache + 缓存三大问题 |
| 01 | [`01_rabbitmq.md`](01_rabbitmq.md) | RabbitMQ 起步 + 异步通知 + 延迟消息 |
| 02 | [`02_schedule_file.md`](02_schedule_file.md) | Spring Task 定时任务 + 文件上传（MinIO） |

## 配套代码

→ [`../../code/week9/`](../../code/week9/)

## 本周里程碑

- 用 Redis 做读多写少的接口缓存
- 解释缓存穿透 / 击穿 / 雪崩 + 各自对策
- 用 RabbitMQ 让"下单 → 发短信" 异步
- 用 Spring Task 写一个每天凌晨 2 点跑的定时任务
- 实现文件上传 + 下载，存到本地或 MinIO
