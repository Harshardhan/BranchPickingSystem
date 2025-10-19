package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.example.demo.excpetions.AnalyticsNotFoundException;
import com.example.demo.excpetions.AnalyticsProcessingException;
import com.example.demo.excpetions.InvalidAnalyticsException;

public interface AnalyticsService {

    // 1️⃣ Record a new analytics entry (from Order/Delivery events)
    Analytics saveAnalytics(Analytics analytics)throws AnalyticsNotFoundException, InvalidAnalyticsException;

    // 2️⃣ Update delivery status (e.g., when delivery is confirmed)
    Analytics updateDeliveryStatus(Long orderId, DeliveryStatus deliveryStatus, LocalDateTime deliveredAt)throws InvalidAnalyticsException, AnalyticsNotFoundException;

    // 3️⃣ Get average delivery duration (in minutes)
    Long getAverageDeliveryTime()throws AnalyticsNotFoundException;

    // 4️⃣ Get delivery duration by product
    Map<Long, Long> getAverageDeliveryTimeByProduct()throws AnalyticsProcessingException;

    // 5️⃣ Get delayed deliveries beyond threshold (e.g. orders longer than X minutes)
    List<Analytics> getDelayedDeliveries(Long thresholdMinutes)throws AnalyticsProcessingException;

    // 6️⃣ Get orders delivered within date range
    List<Analytics> getDeliveredOrdersBetween(LocalDateTime start, LocalDateTime end)throws AnalyticsNotFoundException;

    // 7️⃣ Get delivery success rate (percentage)
    Double getDeliverySuccessRate()throws AnalyticsProcessingException;

    // 8️⃣ Get summary report (combined metrics)
    Map<String, Object> getDeliverySummaryReport()throws AnalyticsNotFoundException, AnalyticsProcessingException;

}
