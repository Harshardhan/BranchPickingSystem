package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);

    @KafkaListener(
        topics = "${kafka.topic.order}",
        groupId = "order-group",
        containerFactory = "orderKafkaListenerContainerFactory"
    )
    public void consumeOrder(Order order) {
        logger.info("Received Order event: {}", order);
        System.out.println("Processing order: " + order.getOrderStatus());
    }

    @KafkaListener(
        topics = "${kafka.topic.notification}",
        groupId = "notification-group",
        containerFactory = "notificationKafkaListenerContainerFactory"
    )
    public void consumeNotification(NotificationRequest notification) {
        logger.info("Received Notification event: {}", notification);
        System.out.println("Processing notification for customer: " + notification.getCustomerId());
    }

    @KafkaListener(
        topics = "${kafka.topic.analytics}",
        groupId = "analytics-group",
        containerFactory = "analyticsKafkaListenerContainerFactory"
    )
    public void consumeAnalytics(Analytics analytics) {
        logger.info("Received Analytics event: {}", analytics);
        System.out.println("Processing analytics delivery status: " + analytics.getDeliveryStatus());
    }
}
