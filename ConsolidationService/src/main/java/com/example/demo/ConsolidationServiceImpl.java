package com.example.demo;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ConsolidationServiceImpl implements ConsolidationService {

	@Autowired
	private ConsolidationRepository consolidationRepository;

	private static final Logger logger = LoggerFactory.getLogger(ConsolidationServiceImpl.class);

	@CircuitBreaker(name = "consolidationService", fallbackMethod = "fallbackFindByOrderReference")
	@Retry(name = "consolidationService")
	@RateLimiter(name = "consolidationService")
	@Bulkhead(name = "consolidationService", type = Bulkhead.Type.SEMAPHORE)
	@Override
	public Optional<Consolidation> findByOrderReference(String orderRef) throws ConsolidationNotFoundException {
	    logger.info("Fetching Consolidation Record for Order Reference: {}", orderRef);
	    return Optional.ofNullable(consolidationRepository.findByOrderReference(orderRef)
	            .orElseThrow(() -> new ConsolidationNotFoundException(
	                    "Consolidation record not found for Order Reference: " + orderRef)));
	}
	@Override
	public Optional<Consolidation> findById(Long id) {
	    logger.info("Find consolidation by ID: {}", id);
	    return consolidationRepository.findById(id);
	}

	public Optional<Consolidation> fallbackFindByOrderReference(String orderReference, Throwable t) {
	    logger.error("Fallback triggered for findByOrderReference({}) due to: {}", orderReference, t.getMessage());
	    return Optional.empty(); // or return a dummy object if needed
	}
	@Override
	public List<Consolidation> findAllConsolidatedOrders() {
		logger.info("Find the consolidated orders is true.");
		return consolidationRepository.findByIsConsolidatedTrue();
	}

	@CircuitBreaker(name = "consolidationService", fallbackMethod = "fallbackFindByCustomerId")
	@Retry(name = "consolidationService")
	@RateLimiter(name = "consolidationService")
	@Bulkhead(name = "consolidationService", type = Bulkhead.Type.SEMAPHORE)
	@Override
	@Transactional
	public List<Consolidation> findByCustomerId(Long customerId) throws ConsolidationNotFoundException {
	    logger.info("Find the customer ID: {}", customerId);

	    List<Consolidation> result = consolidationRepository.findByCustomerId(customerId);
	    if (result.isEmpty()) {
	        logger.error("Failed to find the customer ID: {} ", customerId);
	        throw new ConsolidationNotFoundException("No orders found for Customer ID: " + customerId);
	    }
	    return result;
	}

	public List<Consolidation> fallbackFindByCustomerId(Long customerId, Throwable t) {
	    logger.error("Fallback triggered for findByCustomerId({}) due to: {}", customerId, t.getMessage());
	    return List.of(); // Return empty list or default values
	}
	@Override
	public List<Consolidation> findAllNonConsolidatedOrders() {
		logger.info("Find all non consolidated orders is false.");
		return consolidationRepository.findByIsConsolidatedFalse();
	}

	@Override
	public List<Consolidation> findByOrderStatus(OrderStatus orderStatus) {
		logger.info("Find the status of an order:{} ", orderStatus);
		return consolidationRepository.findByOrderStatus(orderStatus);
	}

	@Override
	public List<Consolidation> findByOrderDateRange(LocalDate startDate, LocalDate endDate) {
		logger.info("Find the order date range between {} and {}", startDate, endDate);
		return consolidationRepository.findByOrderDateBetween(startDate, endDate);
	}

	@Override
	public List<Consolidation> findByDeliveryDate(LocalDate deliveryDate) {
		logger.info("Find the delivery date:{} ", deliveryDate);
		return consolidationRepository.findByDeliveryDate(deliveryDate);
	}

	@Override
	public List<Consolidation> findByPaymentMethod(String paymentMethod) {
		List<Consolidation> payment = consolidationRepository.findByPaymentMethod(paymentMethod);
		logger.info("Find the payment method:{} ", payment.size(), paymentMethod);
		return payment;
	}

	@Override
	public List<Consolidation> findByCurrency(String currency) {
		logger.info("Find the currency:{} ", currency);
		return consolidationRepository.findByCurrency(currency);
	}

	@Override
	public List<Consolidation> findByIsPaid(boolean isPaid) {
		logger.info("Find the payment if  already is done:{} ", isPaid);
		return consolidationRepository.findByIsPaid(isPaid);
	}

	@Override
	public List<Consolidation> findByOptimisedTotalAmountGreaterThan(BigDecimal amount) {
		logger.info("Find Total Amount greater than optimised total amount: {}", amount);
		return consolidationRepository.findByOptimisedTotalAmountGreaterThan(amount);
	}

	@Override
	public List<Consolidation> findByOrderType(String orderType) {
		logger.info("Find the order type:{} ", orderType);
		return consolidationRepository.findByOrderType(orderType);
	}

	@Override
	public List<Consolidation> findByCreatedAtRange(LocalDate start, LocalDate end) {
		logger.info("Find by created at range:{} ", start, end);
		return consolidationRepository.findByCreatedAtBetween(start, end);
	}

	@Override
	public List<Consolidation> searchByRemarks(String keyword) {
		logger.info("find the remarks:{} ", keyword);
		return consolidationRepository.searchByRemarks(keyword);
	}

	@Override
	public Consolidation optimizeOrder(Consolidation consolidation) {
	    logger.info("Optimizing consolidation for orderRef: {}", consolidation.getOrderReference());

	    // Simple business logic
	    BigDecimal baseAmount = consolidation.getOptimisedTotalAmount() != null
	        ? consolidation.getOptimisedTotalAmount()
	        : BigDecimal.valueOf(100);

	    consolidation.setOptimisedTotalAmount(baseAmount);
	    consolidation.setConsolidated(true);
	    consolidation.setRemarks("Auto consolidated");
	    consolidation.setDeliveryDate(LocalDate.now().plusDays(2));

	    Consolidation saved = consolidationRepository.save(consolidation);
	    logger.info("✅ Consolidation saved with ID: {}", saved.getId());

	    return saved;
	}
}
