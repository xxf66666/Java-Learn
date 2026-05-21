package com.learning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Spring AMQP / RabbitMQ 相关类
import org.springframework.amqp.core.*;
// 消费者注解
import org.springframework.amqp.rabbit.annotation.RabbitListener;
// 发消息的工具
import org.springframework.amqp.rabbit.core.RabbitTemplate;
// JSON 消息转换器
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

/**
 * RabbitMQ 配置：声明 Exchange / Queue / Binding
 */
@Configuration
class RabbitConfig {

    // 常量字符串，多处复用避免拼错
    public static final String EXCHANGE = "demo.exchange";
    public static final String QUEUE = "demo.queue";
    public static final String KEY = "demo.key";

    /**
     * 声明一个 direct 类型的 Exchange
     * direct: 按 routing key **精确**匹配 Queue
     * (name, durable=true, autoDelete=false)
     * durable: RabbitMQ 重启后 Exchange 还在
     */
    @Bean public DirectExchange exchange() { return new DirectExchange(EXCHANGE, true, false); }

    /** 声明 Queue，durable=true */
    @Bean public Queue queue() { return new Queue(QUEUE, true); }

    /**
     * 绑定关系：Queue ← (key) ← Exchange
     * 发到 EXCHANGE 且 routing key = KEY 的消息会路由到这个 Queue
     */
    @Bean public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(KEY);
    }

    /**
     * 消息转换器：把对象序列化成 JSON
     * 默认是 JDK 序列化，可读性差且要求实现 Serializable，改成 Jackson 更友好
     */
    @Bean public MessageConverter converter() { return new Jackson2JsonMessageConverter(); }
}

/** 生产者 Controller：POST 一个 JSON 就发消息 */
@RestController
@RequestMapping("/api")
class ProducerController {

    @Autowired RabbitTemplate rabbit;

    @PostMapping("/send")
    public Map<String, Object> send(@RequestBody Map<String, Object> payload) {
        // convertAndSend(交换机, routingKey, 消息对象)
        // 内部会调 MessageConverter 把对象转 JSON 再发出去
        rabbit.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.KEY, payload);
        return Map.of("ok", true);
    }
}

/**
 * 消费者：监听队列，有消息自动调 onMessage
 *
 * @RabbitListener 让 Spring 启动时给这个方法创建监听器
 */
@Component
class DemoConsumer {
    private static final Logger log = LoggerFactory.getLogger(DemoConsumer.class);

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onMessage(Map<String, Object> payload) {
        // Map<String, Object> 参数：Spring AMQP 用 Jackson 自动反序列化
        log.info("📩 收到消息: {}", payload);
    }
}
