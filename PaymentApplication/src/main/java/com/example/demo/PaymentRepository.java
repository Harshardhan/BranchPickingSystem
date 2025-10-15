package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends  JpaRepository<Payment, Long>{

	// Find all payments for a given user
	List<Payment> findByUserId(Long userId);

	// Find all payments for a given order
	List<Payment> findByOrderId(Long orderId);

	// Find payments by status
	List<Payment> findByStatus(PaymentStatus status);

	// Find payments by method (e.g., UPI, CARD)
	
	List<Payment> findPaymentsByMethod(PaymentMethod method);

	
	// Find all payments by user and status
	List<Payment> findByUserIdAndStatus(Long userId, PaymentStatus status);

	// Find all payments by order and status
	List<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);

	// Find payment by user and order (usually unique)
	Optional<Payment> findByUserIdAndOrderId(Long userId, Long orderId);

	// Find payments greater than or equal to a certain amount
	List<Payment> findByAmountGreaterThanEqual(BigDecimal amount);

	// Find payments between two amounts
	List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

	// Find payments after a certain timestamp
	List<Payment> findByPaymentTimestampAfter(LocalDateTime timestamp);

	// Find payments between two dates
	List<Payment> findByPaymentTimestampBetween(LocalDateTime start, LocalDateTime end);
	// Check if a payment exists for given order
	boolean existsByOrderId(Long orderId);


	// Count payments by status
	long countByStatus(PaymentStatus status);

	// Count payments by user
	long countByUserId(Long userId);


}
