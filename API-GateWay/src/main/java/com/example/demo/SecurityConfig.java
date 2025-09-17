package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable) // disable CSRF for APIs
            .authorizeExchange(exchanges -> exchanges
                // allow unauthenticated access
                .pathMatchers("/api/auth/**", "/api/users/register").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                // everything else requires auth (you can change to .permitAll() if testing)
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt); // enable JWT later

        return http.build();
    }
}
