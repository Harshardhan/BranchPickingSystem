package com.example.demo;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.example.demo.Consolidation;

public interface ConsolidationService {

	Optional<Consolidation> findByOrderReference(String orderRef) throws ConsolidationNotFoundException;

	Optional<Consolidation> findById(Long id);

	List<Consolidation> findAllConsolidatedOrders();

	List<Consolidation> findByCustomerId(Long customerId) throws ConsolidationNotFoundException;

	List<Consolidation> findAllNonConsolidatedOrders();

	List<Consolidation> findByOrderStatus(OrderStatus orderStatus);

	List<Consolidation> findByOrderDateRange(LocalDate startDate, LocalDate endDate);

	List<Consolidation> findByDeliveryDate(LocalDate deliveryDate);

	List<Consolidation> findByPaymentMethod(String paymentMethod);

	List<Consolidation> findByCurrency(String currency);

	List<Consolidation> findByIsPaid(boolean isPaid);

	List<Consolidation> findByOptimisedTotalAmountGreaterThan(BigDecimal amount);

	List<Consolidation> findByOrderType(String orderType);

	List<Consolidation> findByCreatedAtRange(LocalDate start, LocalDate end);

	List<Consolidation> searchByRemarks(String keyword);

	Consolidation optimizeOrder( Consolidation consolidation);

	
}
