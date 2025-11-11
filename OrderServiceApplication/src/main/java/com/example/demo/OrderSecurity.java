package com.example.demo;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.example.demo.OrderRepository;

@Component("orderSecurity")
public class OrderSecurity {

    private final OrderRepository orderRepository;

    public OrderSecurity(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // check if the logged-in user owns this order
    public boolean hasAccessToOrder(Long orderId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        String username = authentication.getName(); // from JWT
        var order = orderRepository.findById(orderId);
        return order.isPresent() && 
               (order.get().getUserName().equals(username) || 
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    public boolean hasAccessToOrderReference(String orderReference, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        String username = authentication.getName();
        var order = orderRepository.findByOrderReferenceIgnoreCase(orderReference);
        return order.isPresent() && 
               (order.get().getUserName().equals(username) ||
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}
