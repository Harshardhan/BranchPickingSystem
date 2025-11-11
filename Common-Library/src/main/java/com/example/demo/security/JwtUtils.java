package com.example.demo.security;


import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to extract user details from JWT stored in SecurityContext.
 * Can be reused across all microservices.
 */
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * Extracts the current authenticated user's ID from JWT.
     * 
     * @return userId if available, otherwise null.
     */
    public static Long getAuthenticatedUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                Object userIdClaim = jwt.getClaim("userId"); // 👈 your JWT must contain this claim

                if (userIdClaim != null) {
                    return Long.valueOf(userIdClaim.toString());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to extract userId from JWT: {}", e.getMessage());
        }

        return null;
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("roles"); // matches claim name
        converter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }


}