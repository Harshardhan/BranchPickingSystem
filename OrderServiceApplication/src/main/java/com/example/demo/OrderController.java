package com.example.demo;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.excpetions.InValidOrderException;
import com.example.demo.excpetions.OrderAlreadyExistsException;
import com.example.demo.excpetions.OrderNotFoundException;
import com.example.demo.excpetions.OrderProcessingException;
import com.example.demo.excpetions.UnauthorizedOrderAccessException;
import com.example.demo.security.UserPrincipal;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping()
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<Order> placeOrder(@RequestBody @Valid Order order)
	        throws InValidOrderException, OrderAlreadyExistsException {

		
	    // Extract logged-in user from JWT
	    UserPrincipal principal = (UserPrincipal) SecurityContextHolder
	            .getContext().getAuthentication().getPrincipal();

	    Long tokenUserId = principal.getId();   // ✔ Correct userId
	    String username = principal.getUsername();
	    if (!order.getCustomerId().equals(tokenUserId)) {
	        throw new InValidOrderException("CustomerId mismatch between token and JSON");
	    }

	    // Always override user fields to prevent JSON manipulation
	    order.setCustomerId(tokenUserId);
	    order.setUserName(username);

	    Order createdOrder = orderService.placeOrder(order);

	    return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
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


	@GetMapping()
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
	
	@DeleteMapping("/{id}")
    @PreAuthorize("@orderSecurity.hasAccessToOrder(#orderId, authentication)")

	public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) throws OrderNotFoundException {
	    orderService.deleteOrder(id);
	    logger.info("Order with ID {} deleted successfully", id);
	    return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/reference/{orderReference}")
    @PreAuthorize("@orderSecurity.hasAccessToOrderReference(#orderReference, authentication)")

	public ResponseEntity<Order> findByOrderReference(@PathVariable String orderReference) throws OrderNotFoundException {
	    Order referenceOrder = orderService.findByOrderReference(orderReference);
	    logger.info("Successfully retrieved details of an order with orderReference {}", orderReference);
	    return ResponseEntity.ok(referenceOrder);
	}
	@PutMapping("/{orderId}/process") // ✅ Change "id" to "orderId"
    @PreAuthorize("hasRole('ADMIN')")

	public ResponseEntity<List<Order>> processOrder(@PathVariable("id") Long id) throws OrderProcessingException {
	    List<Order> orderProcess = orderService.processOrder(id);
	    logger.info("Order will be successfully processed with orderId {}", id);
	    return ResponseEntity.ok(orderProcess);
	}
	
	@GetMapping("/{id}")
    @PreAuthorize("@orderSecurity.hasAccessToOrder(#id, authentication)")

	public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id)throws UnauthorizedOrderAccessException {
	    Order order = orderService.getOrderById(id);
	    logger.info("Successfully retrieved order with ID {}", id);
	    return ResponseEntity.ok(order);
	}
}