package com.example.demo;


import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Component;

@Component
public class ProductKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendProductEvent(Object product) {
        kafkaTemplate.send("product-topic", product);
    }
}
