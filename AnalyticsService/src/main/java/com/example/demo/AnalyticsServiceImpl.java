package com.example.demo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.excpetions.AnalyticsNotFoundException;
import com.example.demo.excpetions.AnalyticsProcessingException;
import com.example.demo.excpetions.InvalidAnalyticsException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService{

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceImpl.class);

	
	private  final AnalyticsRepository analyticsRepository;
	
	public AnalyticsServiceImpl(AnalyticsRepository analyticsRepository) {
		this.analyticsRepository = analyticsRepository;
	}

	@Override
	public Analytics saveAnalytics(Analytics analytics) throws AnalyticsNotFoundException, InvalidAnalyticsException {

		if(analytics == null || analytics.getOrderId() ==null) {
			throw new InvalidAnalyticsException("Invalid Details.");
		}
		
	     if (!analyticsRepository.existsById(analytics.getOrderId())) {
	       throw new AnalyticsNotFoundException("Order ID not found: " + analytics.getOrderId());
	    }

	     // ✅ Set createdAt timestamp if null
	     if (analytics.getCreatedAt() == null) {
	         analytics.setCreatedAt(LocalDateTime.now());
	     }

		Analytics savedReport = analyticsRepository.save(analytics);
		logger.info("Generating report on order fullfilment data saved successfully: {} ",savedReport.getId());
		return savedReport;
	}

	@Override
	public Analytics updateDeliveryStatus(Long orderId, DeliveryStatus deliveryStatus, LocalDateTime deliveredAt)
	        throws InvalidAnalyticsException, AnalyticsNotFoundException {

	    Analytics existingReport = analyticsRepository.findByOrderId(orderId)
	            .orElseThrow(() -> new AnalyticsNotFoundException("Analytics not found with Order ID: " + orderId));

	    if (deliveryStatus == null) {
	        throw new InvalidAnalyticsException("Delivery status cannot be null");
	    }

	    // Optional: Prevent downgrading of status
	    if (existingReport.getDeliveryStatus() == DeliveryStatus.DELIVERED &&
	        deliveryStatus != DeliveryStatus.DELIVERED) {
	        throw new InvalidAnalyticsException("Cannot change status from DELIVERED to " + deliveryStatus);
	    }

	    existingReport.setDeliveryStatus(deliveryStatus);
	    existingReport.setOrderDeliveredAt(deliveredAt != null ? deliveredAt : LocalDateTime.now());

	    Analytics updatedReport = analyticsRepository.save(existingReport);

	    logger.info("Updated delivery status for Order ID {}: {}", orderId, deliveryStatus);

	    return updatedReport;
	}
	@Override
	public Long getAverageDeliveryTime() throws AnalyticsNotFoundException {
	    Long avg = analyticsRepository.getAverageDeliveryTime();
	    if (avg == null) {
	        throw new AnalyticsNotFoundException("No delivered orders found to calculate average time!");
	    }
	    logger.info("Successfully find the details of average deliverytime per order:{}");
	    return avg;
	}

	@Override
	public Map<Long, Long> getAverageDeliveryTimeByProduct() throws AnalyticsProcessingException {

		try {
			List<Analytics> deliveredOrders = analyticsRepository.findAll().stream().
					filter(a -> "DELIVERED".equals(a.getDeliveryStatus())).collect(Collectors.toList());
			if(deliveredOrders.isEmpty()) {
	            throw new AnalyticsProcessingException("No delivered orders found to calculate average per product.", null);
			}
			
			Map<Long, Long> averageProduct = deliveredOrders.stream()
					.collect(Collectors.groupingBy(Analytics::getProductId, Collectors.collectingAndThen(
							Collectors.averagingLong(a->Duration.between(a.getOrderPlacedAt(), 
									a.getOrderDeliveredAt()).toMinutes()), Double::longValue))
			
		);
		return averageProduct;
	}catch(Exception e) {
        throw new AnalyticsProcessingException("Failed to calculate average delivery time by product: " + e.getMessage(), e);
	}
	}

	@Override
	public List<Analytics> getDelayedDeliveries(Long thresholdMinutes) throws AnalyticsProcessingException {
	    try {
	        return analyticsRepository.findAll().stream()
	                .filter(a -> "DELIVERED".equals(a.getDeliveryStatus()))
	                .filter(a -> Duration.between(a.getOrderPlacedAt(), a.getOrderDeliveredAt()).toMinutes() > thresholdMinutes)
	                .collect(Collectors.toList());
	    } catch (Exception e) {
	        throw new AnalyticsProcessingException("Failed to retrieve delayed deliveries: " + e.getMessage(), e);
	    }
	}

	@Override
	public List<Analytics> getDeliveredOrdersBetween(LocalDateTime start, LocalDateTime end) 
	        throws AnalyticsNotFoundException {

	    List<Analytics> deliveredOrders = analyticsRepository.findAll().stream()
	            .filter(a -> "DELIVERED".equals(a.getDeliveryStatus()))
	            .filter(a -> !a.getOrderDeliveredAt().isBefore(start) && !a.getOrderDeliveredAt().isAfter(end))
	            .collect(Collectors.toList());

	    if (deliveredOrders.isEmpty()) {
	        throw new AnalyticsNotFoundException("No delivered orders found in the given date range.");
	    }

	    return deliveredOrders;
	}

	@Override
	public Double getDeliverySuccessRate() throws AnalyticsProcessingException {
	    try {
	        List<Analytics> allOrders = analyticsRepository.findAll();
	        if (allOrders.isEmpty()) return 0.0;

	        long deliveredCount = allOrders.stream()
	                .filter(a -> "DELIVERED".equals(a.getDeliveryStatus()))
	                .count();

	        return (deliveredCount * 100.0) / allOrders.size();
	    } catch (Exception e) {
	        throw new AnalyticsProcessingException("Failed to calculate delivery success rate: " + e.getMessage(), e);
	    }
	}
	@Override
	public Map<String, Object> getDeliverySummaryReport() throws AnalyticsNotFoundException, AnalyticsProcessingException {
	    Map<String, Object> summary = new HashMap<>();

	    List<Analytics> allOrders = analyticsRepository.findAll();
	    if (allOrders.isEmpty()) {
	        throw new AnalyticsNotFoundException("No analytics data found to generate summary report.");
	    }

	    summary.put("totalOrders", allOrders.size());
	    summary.put("averageDeliveryTime", getAverageDeliveryTime());
	    summary.put("deliverySuccessRate", getDeliverySuccessRate());
	    summary.put("delayedDeliveries", getDelayedDeliveries(60L)); // Example threshold: 60 minutes
	    summary.put("averageDeliveryTimeByProduct", getAverageDeliveryTimeByProduct());

	    return summary;
	}
}
