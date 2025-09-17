package com.example.demo;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.excpetions.InValidPaymentException;
import com.example.demo.excpetions.PaymentAlreadyExistsException;
import com.example.demo.excpetions.PaymentNotFoundException;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;

	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

	public PaymentServiceImpl(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}

	@Override
	public Payment processPayment(Payment payment) throws InValidPaymentException, PaymentAlreadyExistsException {
		if (payment == null) {
			throw new InValidPaymentException("InValidPayment Details.");
		}
		if (payment.getUserId() == null || payment.getOrderId() == null || payment.getAmount() == null) {
			throw new InValidPaymentException("Missing required payment details.");
		}

		// Ensure order is not already paid
		boolean exists = paymentRepository.existsByOrderId(payment.getOrderId());
		if (exists) {
			throw new PaymentAlreadyExistsException(
					"Payment with orderId " + payment.getOrderId() + " already exists.");
		}

		// set defaults if needed
		if (payment.getStatus() == null) {
			payment.setStatus(PaymentStatus.PENDING);
		}
		if (payment.getPaymentTimestamp() == null) {
			payment.setPaymentTimestamp(LocalDateTime.now());
		}

		Payment savedPayment = paymentRepository.save(payment);
		logger.info("Successfully created payment with id {}", savedPayment.getId());

		return savedPayment;
	}

	@Override
	@Transactional(readOnly = true)

	public Payment getPaymentById(Long paymentId) throws PaymentNotFoundException {

		return paymentRepository.findById(paymentId)
				.orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID " + paymentId));
	}

	@Override
	@Transactional(readOnly = true)

	public List<Payment> getPaymentsByUserId(Long userId) throws PaymentNotFoundException {
		List<Payment> payments = paymentRepository.findByUserId(userId);

		if (payments == null || payments.isEmpty()) {
			logger.warn("No payments found for userId {}", userId);
			throw new PaymentNotFoundException("No payments found for userId " + userId);
		}

		logger.info("Successfully retrieved {} payment(s) for userId {}", payments.size(), userId);
		return payments;
	}

	@Override
	@Transactional(readOnly = true)

	public List<Payment> getPaymentsByOrderId(Long orderId) throws PaymentNotFoundException {
		List<Payment> payments = paymentRepository.findByOrderId(orderId);

		if (payments == null || payments.isEmpty()) {
			logger.warn("No payments found for orderId {}", orderId);
			throw new PaymentNotFoundException("No payments found for orderId " + orderId);
		}

		logger.info("Successfully retrieved {} payment(s) for orderId {}", payments.size(), orderId);
		return payments;
	}

	@Override
	@Transactional(readOnly = true)

	public List<Payment> getPaymentsByStatus(PaymentStatus status) throws PaymentNotFoundException {
		List<Payment> payments = paymentRepository.findByStatus(status);

		if (payments == null || payments.isEmpty()) {
			logger.warn("No payments found for paymentStatus {}", status);
			throw new PaymentNotFoundException("No payments found for paymentStatus " + status);
		}

		logger.info("Successfully retrieved {} payment(s) for paymentStatus {}", payments.size(), status);
		return payments;

	}

	@Override
	@Transactional(readOnly = true)

	public List<Payment> getPaymentsByMethod(PaymentMethod method) throws PaymentNotFoundException {
		List<Payment> payments = paymentRepository.findPaymentsByMethod(method);

		if (payments == null || payments.isEmpty()) {
			logger.warn("No payments found by payments method {}", method);
			throw new PaymentNotFoundException("No payments found by payments method  " + method);
		}

		logger.info("Successfully retrieved {} payment(s) for userId {}", payments.size(), method);
		return payments;

	}

	@Override
	public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) throws PaymentNotFoundException {
		Payment existingPayment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new PaymentNotFoundException("payment not found with ID " + paymentId));
		existingPayment.setStatus(status);
		existingPayment.setPaymentTimestamp(LocalDateTime.now());
		Payment updatedPayment = paymentRepository.save(existingPayment);
		logger.info("Successfully updated payment {} to status {}", paymentId, status);

		return updatedPayment;
	}

	@Override
	public Payment refundPayment(Long paymentId) throws PaymentNotFoundException {

		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new PaymentNotFoundException("Payment not found with Id " + paymentId));

		// Check if payment can be refunded
		if (payment.getStatus() != PaymentStatus.SUCCESS) {
			throw new IllegalStateException("Only successful payments can be refunded");

		}

		payment.setStatus(PaymentStatus.REFUNDED);
		payment.setPaymentTimestamp(LocalDateTime.now()); // update timestamp for refund action

		Payment refundedPayment = paymentRepository.save(payment);

		logger.info("Payment with ID {} has been refunded successfully", paymentId);

		return refundedPayment;

	}

	@Override
	@Transactional(readOnly = true)

	public List<Payment> getPaymentsByAmountRange(BigDecimal min, BigDecimal max) {
		List<Payment> payment = paymentRepository.findByAmountBetween(min, max);
		logger.info("Successfully retrieved payments between {} and {}", min, max);
		return payment;
	}

	@Override
	@Transactional(readOnly = true)

	public List<Payment> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
		List<Payment> payments = paymentRepository.findByPaymentTimestampBetween(start, end);

		logger.info("Retrieved {} payment(s) between {} and {}", payments.size(), start, end);

		return payments;
	}

	@Override
	public boolean paymentExistsForOrder(Long orderId) throws PaymentNotFoundException {
		boolean exists = paymentRepository.existsByOrderId(orderId);

		if (!exists) {
			throw new PaymentNotFoundException("No payment found for orderId " + orderId);
		}

		logger.info("Payment exists for orderId {}", orderId);

		return true;
	}

	@Override
	public long countPaymentsByStatus(PaymentStatus status) {
		long count = paymentRepository.countByStatus(status);

		logger.info("Found {} payment(s) with status {}", count, status);

		return count;
	}

}
