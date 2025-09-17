package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.excpetions.InValidPaymentException;
import com.example.demo.excpetions.PaymentAlreadyExistsException;
import com.example.demo.excpetions.PaymentNotFoundException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ✅ Create a new payment
    @PostMapping
    public ResponseEntity<Payment> processPayment(@RequestBody @Valid Payment payment)
            throws InValidPaymentException, PaymentAlreadyExistsException {
        logger.info("Request received to create payment for orderId={}, userId={}", 
                    payment.getOrderId(), payment.getUserId());
        Payment createdPayment = paymentService.processPayment(payment);
        logger.info("Payment created successfully with id={}", createdPayment.getId());
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    // ✅ Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable("id") Long id)
            throws PaymentNotFoundException {
        logger.info("Fetching payment with id={}", id);
        Payment payment = paymentService.getPaymentById(id);
        logger.info("Successfully retrieved payment with id={}", id);
        return ResponseEntity.ok(payment);
    }

    // ✅ Get payments by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUserId(@PathVariable Long userId)
            throws PaymentNotFoundException {
        logger.info("Fetching payments for userId={}", userId);
        List<Payment> payments = paymentService.getPaymentsByUserId(userId);
        logger.info("Retrieved {} payments for userId={}", payments.size(), userId);
        return ResponseEntity.ok(payments);
    }

    // ✅ Get payments by orderId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrderId(@PathVariable Long orderId)
            throws PaymentNotFoundException {
        logger.info("Fetching payments for orderId={}", orderId);
        List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);
        logger.info("Retrieved {} payments for orderId={}", payments.size(), orderId);
        return ResponseEntity.ok(payments);
    }

    // ✅ Get payments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable PaymentStatus status)
            throws PaymentNotFoundException {
        logger.info("Fetching payments with status={}", status);
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        logger.info("Retrieved {} payments with status={}", payments.size(), status);
        return ResponseEntity.ok(payments);
    }

    // ✅ Get payments by method
    @GetMapping("/method/{method}")
    public ResponseEntity<List<Payment>> getPaymentsByMethod(@PathVariable PaymentMethod method)
            throws PaymentNotFoundException {
        logger.info("Fetching payments with paymentMethod={}", method);
        List<Payment> payments = paymentService.getPaymentsByMethod(method);
        logger.info("Retrieved {} payments with paymentMethod={}", payments.size(), method);
        return ResponseEntity.ok(payments);
    }

    // ✅ Update payment status
    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) throws PaymentNotFoundException {
        logger.info("Request to update status of paymentId={} to {}", id, status);
        Payment updatedPayment = paymentService.updatePaymentStatus(id, status);
        logger.info("Successfully updated paymentId={} to status={}", id, status);
        return ResponseEntity.ok(updatedPayment);
    }

    // ✅ Refund a payment
    @PutMapping("/{id}/refund")
    public ResponseEntity<Payment> refundPayment(@PathVariable Long id)
            throws PaymentNotFoundException {
        logger.info("Request received to refund payment with id={}", id);
        Payment refundedPayment = paymentService.refundPayment(id);
        logger.info("Successfully refunded payment with id={}", id);
        return ResponseEntity.ok(refundedPayment);
    }

    // ✅ Get payments by amount range
    @GetMapping("/amount-range")
    public ResponseEntity<List<Payment>> getPaymentsByAmountRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        logger.info("Fetching payments with amount between {} and {}", min, max);
        List<Payment> payments = paymentService.getPaymentsByAmountRange(min, max);
        logger.info("Retrieved {} payments with amount between {} and {}", payments.size(), min, max);
        return ResponseEntity.ok(payments);
    }

    // ✅ Get payments by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<Payment>> getPaymentsByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        logger.info("Fetching payments between {} and {}", start, end);
        List<Payment> payments = paymentService.getPaymentsByDateRange(start, end);
        logger.info("Retrieved {} payments between {} and {}", payments.size(), start, end);
        return ResponseEntity.ok(payments);
    }

    // ✅ Check if payment exists for an order
    @GetMapping("/exists/{orderId}")
    public ResponseEntity<Boolean> paymentExistsForOrder(@PathVariable Long orderId)
            throws PaymentNotFoundException {
        logger.info("Checking if payment exists for orderId={}", orderId);
        boolean exists = paymentService.paymentExistsForOrder(orderId);
        logger.info("Payment existence check for orderId={} => {}", orderId, exists);
        return ResponseEntity.ok(exists);
    }

    // ✅ Count payments by status
    @GetMapping("/count/{status}")
    public ResponseEntity<Long> countPaymentsByStatus(@PathVariable PaymentStatus status) {
        logger.info("Counting payments with status={}", status);
        long count = paymentService.countPaymentsByStatus(status);
        logger.info("Found {} payments with status={}", count, status);
        return ResponseEntity.ok(count);
    }
}
