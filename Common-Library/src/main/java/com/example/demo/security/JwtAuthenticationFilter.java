package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("[JWT-FILTER] Incoming path = " + path);

        // Skip public endpoints
        if (path.contains("/api/auth") ||
        	    path.contains("/api/users/register") ||
        	    path.contains("/actuator/health")) {
        	    filterChain.doFilter(request, response);
        	    return;
        	}
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            System.out.println("[JWT-FILTER] Already authenticated, skipping");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            System.out.println("[JWT-FILTER] No Authorization header found");
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT-FILTER] Authorization header invalid format");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("[JWT-FILTER] Extracted token = " + token.substring(0, Math.min(20, token.length())) + "...");

        try {
            var claims = jwtTokenProvider.parse(token).getBody();
            var username = claims.getSubject();
            System.out.println("[JWT-FILTER] Token valid for user: " + username);

            var authorities = jwtTokenProvider.getAuthoritiesFromClaims(claims);
            var authentication = new UsernamePasswordAuthenticationToken(
                    username, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("[JWT-FILTER] SecurityContext updated successfully");
        } catch (Exception e) {
            System.out.println("[JWT-FILTER] JWT validation failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }


}
