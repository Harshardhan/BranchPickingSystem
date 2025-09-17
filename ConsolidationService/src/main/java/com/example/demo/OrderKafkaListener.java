package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderKafkaListener.class);

    private final ConsolidationService consolidationService;

    public OrderKafkaListener(ConsolidationService consolidationService) {
        this.consolidationService = consolidationService;
    }

    @KafkaListener(
        topics = "${kafka.topic.order}",
        groupId = "consolidation-service",
        containerFactory = "orderKafkaListenerContainerFactory"
    )
    public void listen(Order order) {
        logger.info("📦 Received order for consolidation: {}", order.getOrderReference());
        Consolidation consolidation = new Consolidation();

        // Map fields from Order to Consolidation
        consolidation.setOrderReference(order.getOrderReference());
        consolidation.setOrderId(order.getId());
        consolidation.setCustomerId(order.getCustomerId());
        consolidation.setOrderType(order.getOrderType());
        consolidation.setPaymentMethod(order.getPaymentMethod());
        consolidation.setOrderStatus(order.getOrderStatus());
        consolidation.setDeliveryAddress(order.getAddress());
        consolidation.setOptimisedItems(order.getProductName());

        Consolidation saved = consolidationService.optimizeOrder(consolidation);
        logger.info("✅ Consolidated order saved: {}", saved.getOrderReference());
    }
}
