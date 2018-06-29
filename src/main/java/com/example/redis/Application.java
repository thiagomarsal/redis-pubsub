package com.example.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisMessageListenerContainer chatContainer(JedisConnectionFactory connectionFactory, ChatReceiver messageListner) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListner, new PatternTopic("chat"));
        container.setErrorHandler(e -> LOGGER.error("There was an error in redis chat listener container", e));

        return container;
    }

    @Bean
    public RedisMessageListenerContainer expiredEventsContainer(JedisConnectionFactory connectionFactory, ExpiredReceiver messageListner) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListner, new PatternTopic("__keyevent@*"));
        container.setErrorHandler(e -> LOGGER.error("There was an error in redis expired listener container", e));

        return container;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(JedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        StringRedisTemplate stringRedisTemplate = ctx.getBean(StringRedisTemplate.class);

        LOGGER.info("Putting message into the chache...");
        String key = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(key, "Hello expirations from Redis! the time is: " + LocalDateTime.now(), 5, TimeUnit.SECONDS);

        LOGGER.info("Sending message to chat...");
        stringRedisTemplate.convertAndSend("chat", "Hello chat from Redis! the time is: " + LocalDateTime.now());

        final String value = stringRedisTemplate.opsForValue().get(key);
        LOGGER.info("Getting message from the chache, key={} value={}", key, value);
    }
}
