package com.example.demo;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;
import com.example.demo.excpetions.InValidPaymentException;
import com.example.demo.excpetions.PaymentAlreadyExistsException;
import com.example.demo.excpetions.PaymentNotFoundException;

public interface PaymentService {

	// Create or update a payment
	Payment processPayment(Payment payment)throws InValidPaymentException, PaymentAlreadyExistsException ;

	// Get a payment by ID
	Payment getPaymentById(Long paymentId)throws PaymentNotFoundException;

	// Get all payments for a user
	List<Payment> getPaymentsByUserId(Long userId)throws PaymentNotFoundException;

	// Get all payments for an order
	List<Payment> getPaymentsByOrderId(Long orderId)throws PaymentNotFoundException;

	// Get payments by status
	List<Payment> getPaymentsByStatus(PaymentStatus status)throws PaymentNotFoundException;


	// Update payment status (ex: from PENDING → SUCCESS)
	Payment updatePaymentStatus(Long paymentId, PaymentStatus status)throws PaymentNotFoundException;

	// Refund a payment
	Payment refundPayment(Long paymentId)throws PaymentNotFoundException;

	// Find payments within an amount range
	List<Payment> getPaymentsByAmountRange(BigDecimal min, BigDecimal max);

	// Find payments within a date range
	List<Payment> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end);

	// Check if payment exists for an order
	boolean paymentExistsForOrder(Long orderId)throws PaymentNotFoundException;

	// Count payments by status
	long countPaymentsByStatus(PaymentStatus status);

	List<Payment> getPaymentsByMethod(PaymentMethod method) throws PaymentNotFoundException;
}