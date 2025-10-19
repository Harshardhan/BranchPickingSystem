package com.example.demo;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableFeignClients
@EnableKafka
public class ProductServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
        logger.info("==== Product Service Started ====");

	}

}
