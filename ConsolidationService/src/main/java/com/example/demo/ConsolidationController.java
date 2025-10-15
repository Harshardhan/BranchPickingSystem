package com.example.demo;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/consolidations")
public class ConsolidationController {
	
	
	@Autowired
	private ConsolidationService consolidationService;
	
	
	private static final Logger logger = LoggerFactory.getLogger(ConsolidationController.class);

	@PostMapping("/optimize")
	public ResponseEntity<Consolidation> optimizeOrder(@RequestBody Consolidation consolidation) {
	    try {
	        String orderReference = consolidation.getOrderReference();
	        logger.info("Received order for consolidation: {}", orderReference);

	        Consolidation newConsolidation = consolidationService.optimizeOrder(consolidation);

	        logger.info("Saved consolidated order: {}", newConsolidation);
	        return ResponseEntity.ok(newConsolidation);
	    } catch (Exception ex) {
	        logger.error("Error during order consolidation: ", ex);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
    
	@GetMapping("/order/{orderReference}")
	public Consolidation getConsolidationByOrderReference(@PathVariable("orderReference") String orderReference) throws ConsolidationNotFoundException {
        return consolidationService.findByOrderReference(orderReference)
            .orElseThrow(() -> new ConsolidationNotFoundException("No consolidation record found for Order Reference: " + orderReference));
    }

	@GetMapping("/all")
	public ResponseEntity<List<Consolidation>> getAllConsolidatedOrders(){
		logger.info("Fetching all consolidated orders.");
		return ResponseEntity.ok(consolidationService.findAllConsolidatedOrders());
	}
	
	@GetMapping("/customer/{customerId}")
	public ResponseEntity<List<Consolidation>> getOrdersByCustomerId(@PathVariable Long customerId) throws ConsolidationNotFoundException{
		logger.info("Find orders by customerId: {}", customerId);
		return ResponseEntity.ok(consolidationService.findByCustomerId(customerId));
	}
	
	@GetMapping("/non-consolidated")
	public ResponseEntity<List<Consolidation>> getAllNonConsolidatedOrders(){
		logger.info("Fetching all non- consolidated orders.");
		return ResponseEntity.ok(consolidationService.findAllNonConsolidatedOrders());
	}
	
	@GetMapping("/status/{orderStatus}")
	public ResponseEntity<List<Consolidation>> getOrdersByStatus(@PathVariable("orderStatus") OrderStatus orderStatus){
		logger.info("Fetching with order status: {}", orderStatus);
		return ResponseEntity.ok(consolidationService.findByOrderStatus(orderStatus));
	}
	
	@GetMapping("/date-range")
	public ResponseEntity<List<Consolidation>> getOrdersByDateRange(@RequestParam("startDate") LocalDate startDate,@RequestParam("endDate") LocalDate endDate){
		
		logger.info("Fetching orders between {} and {}", startDate, endDate);
		return ResponseEntity.ok(consolidationService.findByOrderDateRange(startDate, endDate));
	}
	
	
	@GetMapping("/delivery-date/{deliveryDate}")
	public ResponseEntity<List<Consolidation>> getOrdersByDeliveryDate(@PathVariable("deliveryDate") LocalDate deliveryDate){
		logger.info("Fetching orders with delivery date: {}", deliveryDate);
		return ResponseEntity.ok(consolidationService.findByDeliveryDate(deliveryDate));
	}
	
	@GetMapping("/payment-method/{paymentMethod}")
	public ResponseEntity<List<Consolidation>> getOrdersByPaymentMethod(@PathVariable("paymentMethod") String paymentMethod){
		logger.info("Fetching orders by payment method: {}", paymentMethod);
		return ResponseEntity.ok(consolidationService.findByPaymentMethod(paymentMethod));
		
	}
	
	@GetMapping("/currency/{currency}")
	public ResponseEntity<List<Consolidation>> getOrdersByCurrency(@PathVariable("currency") String currency){
		logger.info("Fethcing orders by currency: {}", currency);
		return ResponseEntity.ok(consolidationService.findByCurrency(currency));
		
	}
	
	@GetMapping("/is-Paid/{isPaid}")
	public ResponseEntity<List<Consolidation>> getOrdersByPaymentStatus(@PathVariable("isPaid") boolean isPaid){
		logger.info("Fetching orders by payment status:{}", isPaid);
		return ResponseEntity.ok(consolidationService.findByIsPaid(isPaid));
	}
	
	@GetMapping("/amount-greater-than/{amount}")
	public ResponseEntity<List<Consolidation>> getOrdersByAmountGreaterThan(@PathVariable("amount") BigDecimal amount){
		logger.info("Fetching orders with amount greater than: {}", amount);
		return ResponseEntity.ok(consolidationService.findByOptimisedTotalAmountGreaterThan(amount));
	}
	@GetMapping("/{id}")
	public ResponseEntity<Consolidation> getConsolidationById(@PathVariable("id") Long id) throws ConsolidationNotFoundException {
	    logger.info("Fetching consolidation by ID: {}", id);
	    return ResponseEntity.ok(
	        consolidationService.findById(id)
	            .orElseThrow(() -> new ConsolidationNotFoundException("No consolidation found for ID: " + id))
	    );
	}

	@GetMapping("/order-type/{orderType}")
	public ResponseEntity<List<Consolidation>> getOrdersByType(@PathVariable("orderType") String orderType){
		logger.info("Fetching orders by type: {}",orderType);
		return ResponseEntity.ok(consolidationService.findByOrderType(orderType));
	}
	
	@GetMapping("/created-range")
	public ResponseEntity<List<Consolidation>> getOrdersByCreatedRange(@RequestParam("start") LocalDate start,@RequestParam("end") LocalDate end){
		logger.info("Fetching orders created between {} and {} ",start, end);
		return ResponseEntity.ok(consolidationService.findByCreatedAtRange(start, end));
	}
	
	@GetMapping("/search-remarks/{keyword}")
	public ResponseEntity<List<Consolidation>> searchByRemarks(@PathVariable("keyword") String keyword){
		logger.info("search orders by remarks keyword{} ", keyword);
		return ResponseEntity.ok(consolidationService.searchByRemarks(keyword));
	}
	
	
}
