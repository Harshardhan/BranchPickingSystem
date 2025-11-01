package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange
                // Public endpoints
                .pathMatchers("/api/users/register", "/api/auth/**", "/actuator/**").permitAll()
                // Allow all others — Gateway just forwards
                .anyExchange().permitAll()
            )
            .build(); // <- we don't call oauth2Login(), httpBasic(), etc.
    }
}
