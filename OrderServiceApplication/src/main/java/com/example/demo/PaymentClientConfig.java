package com.example.demo;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentClientConfig {

    @Bean
    public RequestInterceptor jwtRequestInterceptor() {
        return requestTemplate -> {
            // Put a valid JWT token here (from your Payment Service login/auth)
            String jwtToken = "YOUR_VALID_JWT_TOKEN_HERE";
            requestTemplate.header("Authorization", "Bearer " + jwtToken);
        };
    }
}
