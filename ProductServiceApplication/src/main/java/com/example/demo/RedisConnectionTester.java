package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class RedisConnectionTester {

    private static final Logger logger = LoggerFactory.getLogger(RedisConnectionTester.class);

    private final StringRedisTemplate redisTemplate;
    private final String redisHost;
    private final String redisPassword;

    public RedisConnectionTester(StringRedisTemplate redisTemplate,
                                 @Value("${spring.data.redis.host}") String redisHost,
                                 @Value("${spring.data.redis.password:NOT_SET}") String redisPassword) {
        this.redisTemplate = redisTemplate;
        this.redisHost = redisHost;
        this.redisPassword = redisPassword;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void testRedisConnection() {
        logger.info("🔍 RESOLVED Redis Host: {}", redisHost);
        logger.info("🔐 Loaded Redis password from config: {}", redisPassword);
        try {
            redisTemplate.opsForValue().set("health", "ok");
            String value = redisTemplate.opsForValue().get("health");
            logger.info("✅ Redis test passed. Value: {}", value);
        } catch (Exception e) {
            logger.error("❌ Redis test failed", e);
        }
    }
}
