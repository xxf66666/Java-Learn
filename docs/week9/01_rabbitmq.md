# Week 9 §01 · RabbitMQ

> 消息队列解决：**异步**（下单后慢慢发短信）、**解耦**（订单服务不直接调通知服务）、**削峰**（突发流量先入队列）。

---

## 1. 启动 RabbitMQ

```bash
docker run -d --name rabbit \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=admin \
  -e RABBITMQ_DEFAULT_PASS=admin \
  rabbitmq:3-management

# 管理后台：http://localhost:15672  (admin/admin)
```

---

## 2. 核心概念

```
Producer ──发→ Exchange ──路由→ Queue ──消费→ Consumer
```

| 概念 | 含义 |
|------|------|
| **Producer** | 生产者，发消息 |
| **Exchange** | 交换机，根据规则把消息路由到 Queue |
| **Queue** | 队列，消息暂存的地方 |
| **Binding** | Exchange 和 Queue 的绑定关系 + 路由 key |
| **Consumer** | 消费者，从 Queue 拉消息 |

### Exchange 类型

| 类型 | 路由规则 | 典型场景 |
|------|---------|---------|
| **direct** | 精确匹配 routing key | 点对点 |
| **topic** | 通配符匹配 (`order.*` / `*.notify`) | 多业务路由 |
| **fanout** | 所有绑定的 Queue 都收到 | 广播 |
| **headers** | 按 header 匹配 | 少用 |

---

## 3. Spring Boot 集成

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin
    listener:
      simple:
        acknowledge-mode: auto       # 自动确认（生产推荐 manual 手动确认）
```

---

## 4. 声明交换机和队列（用注解）

```java
@Configuration
public class RabbitConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_NOTIFY_QUEUE = "order.notify";
    public static final String ORDER_NOTIFY_KEY = "order.notify";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }

    @Bean
    public Queue notifyQueue() {
        return new Queue(ORDER_NOTIFY_QUEUE, true);
    }

    @Bean
    public Binding binding(Queue notifyQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(notifyQueue).to(orderExchange).with(ORDER_NOTIFY_KEY);
    }
}
```

---

## 5. 发消息

```java
@Autowired
private RabbitTemplate rabbitTemplate;

public void notifyOrderCreated(Order o) {
    rabbitTemplate.convertAndSend(
        RabbitConfig.ORDER_EXCHANGE,
        RabbitConfig.ORDER_NOTIFY_KEY,
        o);              // RabbitTemplate 自动序列化（默认 JDK；推荐换 Jackson）
}
```

---

## 6. 消费消息

```java
@Component
public class OrderNotifyConsumer {

    @RabbitListener(queues = RabbitConfig.ORDER_NOTIFY_QUEUE)
    public void onOrder(Order o) {
        // 发短信 / 发邮件 / ...
        System.out.println("收到订单通知：" + o.getId());
    }
}
```

---

## 7. JSON 序列化

```java
@Bean
public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
}

@Bean
public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter mc) {
    RabbitTemplate t = new RabbitTemplate(cf);
    t.setMessageConverter(mc);
    return t;
}
```

---

## 8. 延迟消息（订单 30 分钟未支付自动取消）

**方案 A：rabbitmq-delayed-message-exchange 插件**（推荐）

```bash
# 进 rabbitmq 容器
rabbitmq-plugins enable rabbitmq_delayed_message_exchange
```

```java
@Bean
public CustomExchange delayedExchange() {
    return new CustomExchange("order.delay", "x-delayed-message",
        true, false, Map.of("x-delayed-type", "direct"));
}

// 发消息时加 x-delay header
rabbitTemplate.convertAndSend("order.delay", "key", payload, msg -> {
    msg.getMessageProperties().setHeader("x-delay", 30 * 60 * 1000);  // 30 分钟
    return msg;
});
```

**方案 B：死信队列 + TTL**（复杂但通用，不展开）

---

## 9. 可靠性：确认 / 重试 / 死信

| 问题 | 解决 |
|------|------|
| 消息发出去但 broker 没收到 | publisher confirm |
| 消息进 queue 但消费时崩了 | 手动 ack |
| 反复消费失败 | 限制重试次数 → 进死信队列 |
| 消费幂等 | 业务侧加唯一 key 防重 |

```yaml
spring:
  rabbitmq:
    publisher-confirm-type: correlated
    listener:
      simple:
        acknowledge-mode: manual
        retry:
          enabled: true
          max-attempts: 3
```

---

## 10. 自查

- [ ] Docker 跑起 RabbitMQ，登 `http://localhost:15672`
- [ ] 完整跑通本周示例：发消息 → 收到消息
- [ ] 解释 Exchange / Queue / Binding 三者关系
- [ ] 解释 4 种 Exchange 类型分别用在什么场景
- [ ] 写一个延迟 10 秒的消息（用 delayed-message 插件）

## 代码示例

→ [`code/week9/rabbitmq-demo/`](../../code/week9/rabbitmq-demo/)
