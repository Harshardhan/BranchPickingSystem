package com.example.demo;

import java.time.LocalDateTime;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import com.example.demo.excpetions.InValidOrderException;
import com.example.demo.excpetions.OrderAlreadyExistsException;
import com.example.demo.excpetions.OrderNotFoundException;
import com.example.demo.excpetions.UnauthorizedOrderAccessException;
import com.example.demo.security.JwtUtils;

@Service
@Transactional
@Retry(name = "order-service")

public class OrderServiceImpl implements OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	private final OrderRepository orderRepository;
	private final OrderEventPublisher orderEventPublisher;
	private final ProductClient productClient;
	private final PaymentClient paymentClient; // Feign client

	public OrderServiceImpl(OrderRepository orderRepository, OrderEventPublisher orderEventPublisher,
			ProductClient productClient, PaymentClient paymentClient) {
		this.orderRepository = orderRepository;
		this.orderEventPublisher = orderEventPublisher;
		this.productClient = productClient;
		this.paymentClient = paymentClient;
	}

	@Override
	@Transactional
	public Order placeOrder(Order order) {

	    validateOrderBasics(order);
	    Product product = validateProduct(order);
	    checkDuplicateOrder(order, product);

	    order.setProductName(product.getProductName());
	    order.setPrice(product.getPrice()); // Always use DB price for safety
	    order.setOrderReference(UUID.randomUUID().toString());
	    order.setOrderStatus(OrderStatus.PLACED);

	    Order savedOrder = orderRepository.save(order);
	    logger.info("✅ Order saved: {}", savedOrder.getOrderReference());

	    handlePayment(savedOrder);
	    publishEvents(savedOrder);

	    return savedOrder;
	}
	
	private void validateOrderBasics(Order order) {
	    if (order == null || order.getPrice() == null || order.getQuantity() <= 0) {
	        throw new InValidOrderException("Invalid order details.");
	    }
	}
	private Product validateProduct(Order order) {
	    Product product = productClient.getProductById(order.getProductId());

	    if (product == null || product.getId() == null ||
	        !product.getId().equals(order.getProductId()) ||
	        "Fallback Product".equalsIgnoreCase(product.getProductName()) ||
	        "Unknown".equalsIgnoreCase(product.getProductName())) {

	        throw new InValidOrderException("Invalid Product: " + order.getProductId());
	    }
	    return product;
	}
	private void checkDuplicateOrder(Order order, Product product) {
	    if (orderRepository.findByCustomerIdAndProductId(order.getCustomerId(), product.getId()).isPresent()) {
	        throw new OrderAlreadyExistsException("Order already exists for this product");
	    }
	    
	    if (order.getPrice().compareTo(product.getPrice()) != 0) {
	        throw new InValidOrderException("Price mismatch for product");
	    }
	}
	private void handlePayment(Order order) {
	    try {
	        Payment paymentReq = Payment.builder()
	                .userId(order.getCustomerId())
	                .orderId(order.getId())
	                .username(order.getUserName())
	                .method(order.getPaymentMethod())
	                .amount(order.getPrice())
	                .status(PaymentStatus.SUCCESS)
	                .build();

	        Payment paymentResponse = paymentClient.processPayment(paymentReq);

	        if (paymentResponse == null || paymentResponse.getStatus() != PaymentStatus.SUCCESS) {
	            markPaymentFailed(order);
	        }

	    } catch (Exception e) {
	        markPaymentFailed(order);
	        logger.error("Payment Error: {}", e.getMessage());
	    }
	}

	private void markPaymentFailed(Order order) {
	    order.setOrderStatus(OrderStatus.PAYMENT_FAILED);
	    orderRepository.save(order);
	}
	private void publishEvents(Order order) {
	    publishOrderEvent(order);
	    publishNotificationEvent(order);
	}

	private void publishOrderEvent(Order order) {
	    try {
	        orderEventPublisher.publishOrder(order);
	    } catch (Exception e) {
	        logger.error("Failed to publish order event: {}", e.getMessage());
	    }
	}

	private void publishNotificationEvent(Order order) {
	    try {
	        NotificationRequest notification = new NotificationRequest();
	        orderEventPublisher.publishNotification(notification);
	    } catch (Exception e) {
	        logger.warn("Failed to publish notification event: {}", e.getMessage());
	    }
	}

	
	// Other methods like updateOrder(), deleteOrder(), etc., remain the same

	@Override
	public List<Order> processOrder(Long orderId) {
		logger.warn("processOrder is not yet implemented for orderId: {}", orderId);
		return Collections.emptyList(); // Instead of throwing an exception
	}

	@Override
	public Order updateOrder(Long id, Order updatedOrder) throws OrderNotFoundException {
		// Step 1: Fetch the existing order
		Order existingOrder = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));

		// Step 2: Update only non-null fields
		if (updatedOrder.getDescription() != null) {
			existingOrder.setDescription(updatedOrder.getDescription());
		}
		if (updatedOrder.getQuantity() > 0) {
			existingOrder.setQuantity(updatedOrder.getQuantity());
		}
		if (updatedOrder.getPrice() != null) {
			existingOrder.setPrice(updatedOrder.getPrice());
		}
		if (updatedOrder.getOrderType() != null) {
			existingOrder.setOrderType(updatedOrder.getOrderType());
		}
		if (updatedOrder.getPaymentMethod() != null) {
			existingOrder.setPaymentMethod(updatedOrder.getPaymentMethod());
		}
		if (updatedOrder.getAddress() != null) {
			existingOrder.setAddress(updatedOrder.getAddress());
		}
		if (updatedOrder.getOrderStatus() != null) {
			existingOrder.setOrderStatus(updatedOrder.getOrderStatus());
		}

		// Step 3: Save the updated order in the database
		Order savedOrder = orderRepository.save(existingOrder);

		// Logging
		logger.info("Order with ID {} updated successfully", id);

		return savedOrder;
	}

	@Override
	public void deleteOrder(Long id) throws OrderNotFoundException {
		Optional<Order> order = orderRepository.findById(id);
		if (order.isEmpty()) {
			logger.error("Attempted to delete non-existent order with id {}", id);
			throw new OrderNotFoundException("Order with id " + id + " not found.");
		}

		orderRepository.deleteById(id);
		logger.info("Successfully deleted order: {}", order.get());
	}

	@Override
	public Order getOrderById(Long id) throws UnauthorizedOrderAccessException {
		logger.info("Fetching order with ID {}", id);

		Long authenticatedUserId = JwtUtils.getAuthenticatedUserId();
		String authenticatedUserRole = JwtUtils.getAuthenticatedUserRole(); // implement this

		if (authenticatedUserId == null) {
			throw new UnauthorizedOrderAccessException("User not authenticated or token invalid.");
		}

		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNotFoundException("Order not found with ID " + id));

		// Authorization check: allow owner or admin role
		if (!authenticatedUserId.equals(order.getCustomerId()) && !"ROLE_ADMIN".equals(authenticatedUserRole)) {
			logger.warn("User {} tried to access order {} belonging to customer {}", authenticatedUserId, id,
					order.getCustomerId());
			throw new UnauthorizedOrderAccessException("You are not authorized to view this order.");
		}

		logger.info("✅ Order {} retrieved successfully for user {}", id, authenticatedUserId);
		return order;
	}

	@Override
	public Order findByOrderReference(String orderReference) throws OrderNotFoundException {
		return orderRepository.findByOrderReferenceIgnoreCase(orderReference).orElseThrow(() -> {
			logger.error("Order not found for reference: {}", orderReference);
			return new OrderNotFoundException("Order with reference " + orderReference + " not found.");
		});
	}

	@Override
	@CircuitBreaker(name = "OrderService", fallbackMethod = "getOrderFallback")
	@Retry(name = "OrderService")
	@RateLimiter(name = "OrderService")
	public List<Order> findByCustomerId(Long customerId) throws OrderNotFoundException {
		List<Order> orders = orderRepository.findByCustomerId(customerId);
		if (orders.isEmpty()) {
			logger.warn("No orders found for customerId {}", customerId);
			throw new OrderNotFoundException("No orders found for customerId " + customerId);
		}
		return orders;
	}

	public List<Order> getOrderFallback(Long customerId, Throwable t) {
		logger.error("Fallback triggered for findByCustomerId with customerId {} due to {}", customerId,
				t.getMessage());
		return Collections.emptyList();
	}

	@Override
	public List<Order> getAllOrders() {
		List<Order> orders = orderRepository.findAll();
		logger.info("Successfully retrieved {} orders", orders.size());
		return orders;
	}

}
