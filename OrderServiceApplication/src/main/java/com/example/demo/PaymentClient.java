package com.example.demo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.github.resilience4j.retry.annotation.Retry;

@Retry(name ="order-service")
@FeignClient(
	    name = "PAYMENT-SERVICE",
	    fallback = PaymentFallback.class,
	    configuration = PaymentClientConfig.class
	)
	public interface PaymentClient {
	    @PostMapping("/api/payments")
	    Payment processPayment(@RequestBody Payment payment);
	}
