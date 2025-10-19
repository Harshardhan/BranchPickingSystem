package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsEventListener {

	private static final Logger logger = LoggerFactory.getLogger(AnalyticsEventListener.class);
	
    // ✅ Use the correct containerFactory bean name
    @KafkaListener(
        topics = "${kafka.topic.analytics}",
        groupId = "analytics-group",
        containerFactory = "analyticsKafkaListenerContainerFactory")
        public void consumerAnalyticsEvent(Analytics analytics) {
            logger.info("📥 Received Analytics event: {}", analytics);
            System.out.println("📢 Sending notification to customer: " + analytics.getDeliveryStatus());
        }

    // ✅ Use the correct containerFactory bean name
    @KafkaListener(
        topics = "${kafka.topic.order}",
        groupId = "order-group",
        containerFactory = "orderKafkaListenerContainerFactory"
    )
    public void consumeOrderEvent(Order order) {
        logger.info("📥 Received Order event: {}", order);
        System.out.println("📢 Sending notification to customer: " + order.getOrderStatus());
    }



}
