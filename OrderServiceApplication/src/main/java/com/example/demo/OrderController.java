package com.example.demo;

import java.net.URI;

import com.example.demo.security.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.excpetions.IdentityMismatchException;
import com.example.demo.excpetions.InValidOrderException;
import com.example.demo.excpetions.OrderAlreadyExistsException;
import com.example.demo.excpetions.OrderNotFoundException;
import com.example.demo.excpetions.OrderProcessingException;
import com.example.demo.excpetions.UnauthorizedOrderAccessException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired

	private  OrderService orderService;

	private static  Logger logger = LoggerFactory.getLogger(OrderController.class);

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Order> placeOrder(@RequestBody @Valid Order order)
	        throws InValidOrderException, OrderAlreadyExistsException, NumberFormatException {

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

	    Long tokenUserId = principal.getId();
	    String username = principal.getUsername();

	    // ❌ Prevent spoofing before setting values
	    if (order.getCustomerId() != null || order.getUserName() != null) {
	        throw new IdentityMismatchException(
	                "Do not include identity fields (customerId, userName) in request.");
	    }

	    // ❌ Admin should NOT place orders as customer
	    boolean isAdmin = auth.getAuthorities()
	            .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

	    if (isAdmin) {
	        throw new IdentityMismatchException(
	                "Admins are not allowed to place customer orders.");
	    }

	    // Validate request details
	    if (order.getProductId() == null || order.getQuantity() <= 0) {
	        throw new InValidOrderException("Product and quantity must be valid.");
	    }

	    // ✅ Set identity safely from JWT
	    order.setCustomerId(tokenUserId);
	    order.setUserName(username);

	    Order createdOrder = orderService.placeOrder(order);

	    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
	            .path("/{id}").buildAndExpand(createdOrder.getId()).toUri();

	    return ResponseEntity.created(location).body(createdOrder);
	}
	@GetMapping("/customer/{customerId}")
	@PreAuthorize("#customerId == authentication.principal.id or hasRole('ADMIN')")
	public ResponseEntity<List<Order>> findByCustomerId(@PathVariable Long customerId)throws OrderNotFoundException{
		List<Order> orders = orderService.findByCustomerId(customerId);
		if (orders.isEmpty()) {
			logger.error("Failed to retrieve details: No orders found for customerId {}", customerId);
			throw new OrderNotFoundException("No orders found for customerId " + customerId);
		}
		logger.info("Successfully retrieved {} orders for customerId {}", orders.size(), customerId);

		return new ResponseEntity<>(orders, HttpStatus.OK);
	}


	@GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")

	public ResponseEntity<List<Order>> getAllOrders() {
		List<Order> orders = orderService.getAllOrders();

		if (orders.isEmpty()) {
			logger.warn("No orders found in the system.");
			return ResponseEntity.noContent().build();
		}

		logger.info("Successfully retrieved {} orders", orders.size());
		return ResponseEntity.ok(orders);
	}
	
	@PutMapping("/{id}")
    @PreAuthorize("@orderSecurity.hasAccessToOrder(#orderId, authentication)")

	public ResponseEntity<Order> updateOrder(@PathVariable("id") Long orderId, @RequestBody @Valid Order updatedOrder) throws OrderNotFoundException {
	    Order updated = orderService.updateOrder(orderId, updatedOrder);  // ✅ Correct: Return single Order
	    logger.info("Successfully updated order with orderId {}", orderId);
	    return ResponseEntity.ok(updated);
	}
	
	// processOrder
	@PutMapping("/{orderId}/process")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Order>> processOrder(@PathVariable("orderId") Long orderId) throws OrderProcessingException {
	    List<Order> orderProcess = orderService.processOrder(orderId);
	    logger.info("Order will be successfully processed with orderId {}", orderId);
	    return ResponseEntity.ok(orderProcess);
	}

	// deleteOrder: use orderId variable everywhere
	@DeleteMapping("/{orderId}")
	@PreAuthorize("@orderSecurity.hasAccessToOrder(#orderId, authentication)")
	public ResponseEntity<Void> deleteOrder(@PathVariable("orderId") Long orderId) throws OrderNotFoundException {
	    orderService.deleteOrder(orderId);
	    logger.info("Order with ID {} deleted successfully", orderId);
	    return ResponseEntity.noContent().build();
	}
	@GetMapping("/reference/{orderReference}")
    @PreAuthorize("@orderSecurity.hasAccessToOrderReference(#orderReference, authentication)")

	public ResponseEntity<Order> findByOrderReference(@PathVariable String orderReference) throws OrderNotFoundException {
	    Order referenceOrder = orderService.findByOrderReference(orderReference);
	    logger.info("Successfully retrieved details of an order with orderReference {}", orderReference);
	    return ResponseEntity.ok(referenceOrder);
	}
	@GetMapping("/{id}")
    @PreAuthorize("@orderSecurity.hasAccessToOrder(#id, authentication)")

	public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id)throws UnauthorizedOrderAccessException {
	    Order order = orderService.getOrderById(id);
	    logger.info("Successfully retrieved order with ID {}", id);
	    return ResponseEntity.ok(order);
	}
}