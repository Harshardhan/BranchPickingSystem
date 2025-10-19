package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.excpetions.AnalyticsNotFoundException;
import com.example.demo.excpetions.AnalyticsProcessingException;
import com.example.demo.excpetions.InvalidAnalyticsException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

	private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
	
	private final AnalyticsService analyticsService;

	public AnalyticsController(AnalyticsService analyticsService) {
		this.analyticsService = analyticsService;
	}
	
	@PostMapping()
	public ResponseEntity<Analytics> saveAnalytics(@RequestBody @Valid Analytics analytics)throws AnalyticsNotFoundException, InvalidAnalyticsException{
		Analytics savedReport = analyticsService.saveAnalytics(analytics);
		logger.info("Generating report on order fullfilment data saved successfully: {} ",savedReport.getId());
		return new ResponseEntity<>(savedReport, HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Analytics> updateDeliveryStatus(@PathVariable("id")Long orderId, @RequestBody @Valid DeliveryStatus deliveryStatus, @RequestBody @Valid LocalDateTime deliveredAt)throws AnalyticsNotFoundException, InvalidAnalyticsException{
		Analytics updatedAnalytics = analyticsService.updateDeliveryStatus(orderId, deliveryStatus, deliveredAt);
		logger.info("Analytics data updated with deliveryStatus and orderId:{}", orderId, deliveryStatus, deliveredAt);
		return ResponseEntity.ok(updatedAnalytics);
	}
	
	@GetMapping("/time")
	public ResponseEntity<Long> getAverageDeliveryTime() throws AnalyticsNotFoundException {
	    Long avgDeliveryTime = analyticsService.getAverageDeliveryTime();
	    logger.info("Successfully retrieved average delivery time per order: {}", avgDeliveryTime);
	    return ResponseEntity.ok(avgDeliveryTime);
	}
	
	@GetMapping("/product")
	public ResponseEntity<Map<Long, Long>> getAverageDeliveryTimeByProduct()throws AnalyticsProcessingException{
		Map<Long, Long> avgDeliveryTimeByProduct = analyticsService.getAverageDeliveryTimeByProduct();
		logger.info("To calculate average delivery time by product: ");
		return ResponseEntity.ok(avgDeliveryTimeByProduct);
	}
	
	@GetMapping("/thresholdMinutes/{thresholdMinutes}")
	public ResponseEntity<List<Analytics>> getDelayedDeliveries(@PathVariable Long thresholdMinutes)throws AnalyticsProcessingException{
		List<Analytics> getDelayedDeliveries = analyticsService.getDelayedDeliveries(thresholdMinutes);
		logger.warn("Failed to retrieve delayed deliveries: "+thresholdMinutes);
		return ResponseEntity.ok(getDelayedDeliveries);
	}
	
	@GetMapping("/deliveredOrders/{start}/{end}")
	public ResponseEntity<List<Analytics>> getDeliveredOrdersBetween(@PathVariable LocalDateTime start, @PathVariable LocalDateTime end)throws AnalyticsNotFoundException{
		List<Analytics> getDeliveredOrders = analyticsService.getDeliveredOrdersBetween(start, end);
		logger.info("delivered orders found in the given date range.");
		return ResponseEntity.ok(getDeliveredOrders);
	}
	
	@GetMapping("/successRate")
	public ResponseEntity<Double> getDeliverySuccessRate()throws AnalyticsProcessingException{
		Double getDeliverySuccessRate = analyticsService.getDeliverySuccessRate();
		logger.info("Successfully to calculate delivery success rate:{} ", getDeliverySuccessRate);
		return ResponseEntity.ok(getDeliverySuccessRate);
	}
	
	@GetMapping("/deliveryReport")
	public ResponseEntity<Map<String, Object>>getDeliverySummaryReport()throws AnalyticsNotFoundException, AnalyticsProcessingException{
		Map<String, Object> summaryReport = analyticsService.getDeliverySummaryReport();
		logger.info("analytics data found to generate summary report.");
		return ResponseEntity.ok(summaryReport);
	}
}
