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
        System.out.println("[JWT-FILTER] Incoming Path: " + path);

        // ✅ Allow public endpoints without token
        if (path.startsWith("/api/auth/login")
                || path.equals("/api/users/register")
                || path.startsWith("/actuator")
                || request.getMethod().equals("OPTIONS")) {

            System.out.println("[JWT-FILTER] Public endpoint → skip token validation");
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ If token is missing → do NOT block (let controller security decide)
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT-FILTER] No token → continuing request without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            var claims = jwtTokenProvider.parse(token).getBody();
            String username = claims.getSubject();
            Long userId = jwtTokenProvider.getUserIdFromClaims(claims);
            var authorities = jwtTokenProvider.getAuthoritiesFromClaims(claims);

         // Create a principal object with both id and username
            UserPrincipal principal = new UserPrincipal(userId, username);

            // Create authentication with correct parameters
            var authentication = new UsernamePasswordAuthenticationToken(
                    principal,  // principal (has id and username)
                    null,       // no credentials
                    authorities // user roles
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("[JWT-FILTER] Token valid → Authenticated: " + username);
        

        } catch (Exception e) {
            System.out.println("[JWT-FILTER] Invalid Token: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
    
    
}
