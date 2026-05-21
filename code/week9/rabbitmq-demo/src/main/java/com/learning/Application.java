package com.learning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

@Configuration
class RabbitConfig {
    public static final String EXCHANGE = "demo.exchange";
    public static final String QUEUE = "demo.queue";
    public static final String KEY = "demo.key";

    @Bean public DirectExchange exchange() { return new DirectExchange(EXCHANGE, true, false); }
    @Bean public Queue queue() { return new Queue(QUEUE, true); }
    @Bean public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(KEY);
    }
    @Bean public MessageConverter converter() { return new Jackson2JsonMessageConverter(); }
}

@RestController
@RequestMapping("/api")
class ProducerController {
    @Autowired RabbitTemplate rabbit;

    @PostMapping("/send")
    public Map<String, Object> send(@RequestBody Map<String, Object> payload) {
        rabbit.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.KEY, payload);
        return Map.of("ok", true);
    }
}

@Component
class DemoConsumer {
    private static final Logger log = LoggerFactory.getLogger(DemoConsumer.class);

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onMessage(Map<String, Object> payload) {
        log.info("📩 收到消息: {}", payload);
    }
}
