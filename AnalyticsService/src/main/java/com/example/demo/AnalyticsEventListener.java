package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsEventListener {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsEventListener.class);

    @KafkaListener(
        topics = "${kafka.topic.analytics}",
        groupId = "analytics-group",
        containerFactory = "analyticsKafkaListenerContainerFactory"
    )
    public void consumeAnalytics(Analytics analytics) {
        logger.info("Received Analytics event: {}", analytics);
        System.out.println("Processing analytics delivery status: " + analytics.getDeliveryStatus());
    }

    @KafkaListener(
        topics = "${kafka.topic.order}",
        groupId = "order-group",
        containerFactory = "orderKafkaListenerContainerFactory"
    )
    public void consumeOrder(Order order) {
        logger.info("Received Order event: {}", order);
        System.out.println("Processing order status: " + order.getOrderStatus());
    }
}
