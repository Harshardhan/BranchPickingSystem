package com.example.demo;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.example.demo.OrderService;
import com.example.demo.security.*;
@Component("orderSecurity")

public class OrderSecurity {

    private final OrderService orderService;

    public OrderSecurity(OrderService orderService) {
        this.orderService = orderService;
    }

    public boolean hasAccessToOrder(Long orderId, Authentication authentication) {
        Long authenticatedUserId = JwtUtils.getAuthenticatedUserId();

        if (authentication == null || authenticatedUserId == null) return false;

        // Admins always allowed
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // Otherwise check if this order belongs to the authenticated user
        Long orderOwnerId = orderService.getOrderById(orderId).getCustomerId();
        return authenticatedUserId.equals(orderOwnerId);
    }

    public boolean hasAccessToOrderReference(String orderReference, Authentication authentication) {
        Long authenticatedUserId = JwtUtils.getAuthenticatedUserId();

        if (authentication == null || authenticatedUserId == null) return false;

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        Long orderOwnerId = orderService.findByOrderReference(orderReference).getCustomerId();
        return authenticatedUserId.equals(orderOwnerId);
    }
}
