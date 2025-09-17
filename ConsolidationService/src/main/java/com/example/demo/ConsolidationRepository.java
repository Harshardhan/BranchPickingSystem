package com.example.demo;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ConsolidationRepository extends JpaRepository<Consolidation, Long> {

	@Query("SELECT c FROM Consolidation c WHERE c.orderReference = :orderReference")
	Consolidation optimizeOrder(@Param("orderReference") String orderReference);

	// find a consolidation recprd by order reference

	Optional<Consolidation> findByOrderReference(String orderRef);
	
	// Find all consolidated orders that are marked as consolidated
	List<Consolidation> findByIsConsolidatedTrue();
	
	//Find all consolidations by a specific customer
	List<Consolidation> findByCustomerId(Long customerId);
	
	//Fina all non- consolidated orders
	List<Consolidation> findByIsConsolidatedFalse();
	
	// Find all orders by a specific order status
	List<Consolidation> findByOrderStatus(OrderStatus orderStatus);
	
	//Find orders within a specific order date range
	List<Consolidation> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
	
	// Find all orders scheduled for delivery on a specific date
	List<Consolidation> findByDeliveryDate(LocalDate deliveryDate);
	
	//Find all orders with a specific payment method
	List<Consolidation> findByPaymentMethod(String paymentMethod);
	
	//Find All orders By currency type
	List<Consolidation> findByCurrency(String currency);
	
	//Find All paid or unpaid orders
	List<Consolidation> findByIsPaid(boolean isPaid);
	
	// Find orders with an optimised total amount greaterthan a certain value
	@Query("SELECT c FROM Consolidation c WHERE c.optimisedTotalAmount > :amount")
	List<Consolidation> findByOptimisedTotalAmountGreaterThan(@Param("amount") BigDecimal amount);
	
	//Find orders by ordertype(online, pick-store)
	List<Consolidation> findByOrderType(String orderType);

	//Find All orders created within a specific time frame
	List<Consolidation> findByCreatedAtBetween(LocalDate start, LocalDate end);
	
    // Custom Query: Find consolidated orders that contain a specific keyword in remarks
	@Query("SELECT c FROM Consolidation c WHERE c.remarks LIKE %:remarks%")
	List<Consolidation> searchByRemarks(@Param("remarks") String remarks);
}
