package com.example.demo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private static Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Long getAuthenticatedUserId() {
        try {
            Authentication auth = getAuth();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
                return principal.getId();
            }
        } catch (Exception e) {
            logger.error("Failed to get User ID: {}", e.getMessage());
        }
        return null;
    }

    public static String getAuthenticatedUsername() {
        Authentication auth = getAuth();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getUsername();
        }
        return null;
    }

    public static String getAuthenticatedUserRole() {
        Authentication auth = getAuth();
        if (auth != null && auth.getAuthorities() != null) {
            return auth.getAuthorities()
                    .stream()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
