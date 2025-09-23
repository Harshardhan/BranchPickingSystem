package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentFallback implements PaymentClient {

    private static final Logger logger = LoggerFactory.getLogger(PaymentFallback.class);


	@Override
	public PaymentRequest processPayment(PaymentRequest payment) {
        logger.warn("Payment service is unavailable. Falling back...");
        PaymentRequest fallbackPayment = new PaymentRequest();
        fallbackPayment.setUsername(payment.getUsername());
        fallbackPayment.setUserId(payment.getUserId());
        fallbackPayment.setOrderId(payment.getOrderId());
		return fallbackPayment;
	}
}
