package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @Autowired
    private NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(
    	    topics = "${kafka.topic.notification}",
    	    groupId = "notification-service",
    	    containerFactory = "notificationKafkaListenerContainerFactory"
    	)
    public void listen(NotificationRequest request) {
    	logger.info("📩 Received Notification for customerId: {}", request.getCustomerId());
    	if (request.getCustomerId() == null || request.getEmail() == null) {
    	    logger.warn("❗ Missing required fields in NotificationRequest: {}", request);
    	    return;
    	}

        try {
            Notification notification = Notification.builder()
                    .customerId(request.getCustomerId())
                    .orderId(request.getOrderId())
                    .productId(request.getProductId())
                    .orderReference(request.getOrderReference())
                    .email(request.getEmail())
                    .message(request.getMessage())
                    .address(request.getAddress())
                    .paymentMethod(request.getPaymentMethod())
                    .price(request.getPrice())
                    .quantity(request.getQuantity())
                    .type(request.getType())
                    .build();

            notificationService.sendNotification(notification);

        } catch (Exception e) {
        	logger.error("❌ Failed to process notification: {}", e.getMessage(), e);
        }
    }

}
