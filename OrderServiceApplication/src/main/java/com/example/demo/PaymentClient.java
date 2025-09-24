package com.example.demo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE", fallback = PaymentFallback.class)
public interface PaymentClient {

    @PostMapping("/api/payments/process")
    Payment processPayment(@RequestBody Payment payment);

}