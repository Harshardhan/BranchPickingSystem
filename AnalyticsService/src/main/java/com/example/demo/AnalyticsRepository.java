package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {

    // 1️⃣ Find all delivered orders within a date range
    List<Analytics> findByDeliveryStatusAndOrderDeliveredAtBetween(
            DeliveryStatus deliveryStatus, LocalDateTime start, LocalDateTime end);

    Optional<Analytics> findByOrderId(Long orderId);
    
    // 2️⃣ Calculate average delivery duration for delivered orders
    @Query("SELECT AVG(a.deliveryDurationMinutes) FROM Analytics a WHERE a.deliveryStatus = 'DELIVERED'")
    Long getAverageDeliveryTime();

    // 3️⃣ Get delayed deliveries beyond threshold (e.g. > 3 days / 4320 minutes)
    @Query("SELECT a FROM Analytics a WHERE a.deliveryDurationMinutes > :threshold")
    List<Analytics> findDelayedDeliveries(Long threshold);

    // 4️⃣ Delivery performance by product
    @Query("SELECT a.productId, AVG(a.deliveryDurationMinutes) FROM Analytics a WHERE a.deliveryStatus = 'DELIVERED' GROUP BY a.productId")
    List<Analytics> getAverageDeliveryTimeByProduct();

    // 5️⃣ Delivery performance by region (if region exists in entity)
    @Query("SELECT a.region, AVG(a.deliveryDurationMinutes) FROM Analytics a WHERE a.deliveryStatus = 'DELIVERED' GROUP BY a.region")
    List<Analytics> getAverageDeliveryTimeByRegion();

    // 6️⃣ Total orders vs Delivered count
    @Query("SELECT COUNT(a) FROM Analytics a WHERE a.deliveryStatus = 'DELIVERED'")
    Long getDeliveredCount();

    @Query("SELECT COUNT(a) FROM Analytics a")
    Long getTotalOrders();
}
