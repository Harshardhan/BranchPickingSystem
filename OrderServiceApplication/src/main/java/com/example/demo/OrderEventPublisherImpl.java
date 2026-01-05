package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
@Component

public class OrderEventPublisherImpl implements OrderEventPublisher {

    private static  Logger logger = LoggerFactory.getLogger(OrderEventPublisherImpl.class);
    private  final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.order}")
    private String orderTopic;

    @Value("${kafka.topic.notification}")
    private String notificationTopic;

    @Value("${kafka.topic.payment}")
    private String paymentTopic;

    @Value("${kafka.topic.analytics}")
    private String analyticsTopic;

    public OrderEventPublisherImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrder(Order order) {
        kafkaTemplate.send(orderTopic, order.getCustomerId().toString(), order);
        logger.info("Order event published to topic '{}': {}", orderTopic, order);
    }

    public void publishNotification(NotificationRequest notification) {
        kafkaTemplate.send(notificationTopic, String.valueOf(notification.getCustomerId()), notification);
        logger.info("Notification event published to topic '{}': {}", notificationTopic, notification);
    }

    public void publishPayment(Payment payment) {
        kafkaTemplate.send(paymentTopic, String.valueOf(payment.getUserId()), payment);
        logger.info("Payment event published to topic '{}': {}", paymentTopic, payment);
    }

    public void publishAnalytics(Analytics analytics) {
        kafkaTemplate.send(analyticsTopic, String.valueOf(analytics.getId()), analytics);
        logger.info("Analytics event published to topic '{}': {}", analyticsTopic, analytics);
    }
}
