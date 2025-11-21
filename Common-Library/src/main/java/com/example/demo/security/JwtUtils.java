package com.example.demo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.List;

public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // 🔹 Get Authentication object
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // 🔹 Retrieve JWT from Authentication principal
    public static Jwt getJwt() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }

    // 🔹 Get User ID from JWT claims
    public static Long getAuthenticatedUserId() {
        try {
            Jwt jwt = getJwt();
            if (jwt != null) {
                Object userIdClaim = jwt.getClaim("userId");
                if (userIdClaim != null) {
                    return Long.valueOf(userIdClaim.toString());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to extract userId: {}", e.getMessage());
        }
        return null;
    }

    // 🔹 Get Username from JWT
    public static String getAuthenticatedUsername() {
        Jwt jwt = getJwt();
        return jwt != null ? jwt.getClaim("username") : null;
    }

    // 🔹 Check if user has a specific role
    public static boolean hasRole(String role) {
        try {
            Jwt jwt = getJwt();
            if (jwt != null) {
                List<String> roles = jwt.getClaimAsStringList("roles");
                return roles != null && roles.contains(role);
            }
        } catch (Exception e) {
            logger.error("Failed to check role: {}", e.getMessage());
        }
        return false;
    }

    // 🔹 Return first available role (for UI behavior or logging)
    public static String getAuthenticatedUserRole() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities()
                    .stream()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    // 🔹 Extract roles from JWT for Spring Security authorization
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("roles");
        converter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);

        return jwtConverter;
    }
}
