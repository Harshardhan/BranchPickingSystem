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

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final ProductClient productClient;
    private final PaymentClient paymentClient; // Feign client

    public OrderServiceImpl(OrderRepository orderRepository, 
                            OrderEventPublisher orderEventPublisher,
                            ProductClient productClient,
                            PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
        this.productClient = productClient;
        this.paymentClient = paymentClient;
    }

    @Override
    public Order placeOrder(Order order) throws InValidOrderException, OrderAlreadyExistsException {
        // 1. Validation
        if (order == null || order.getCustomerId() == null || 
            order.getPrice() == null || order.getQuantity() <= 0) {
            throw new InValidOrderException("Invalid order details.");
        }

        order.setOrderReference(UUID.randomUUID().toString());

        if (orderRepository.findByOrderReferenceIgnoreCase(order.getOrderReference()).isPresent()) {
            throw new OrderAlreadyExistsException("Order already exists.");
        }

        // 2. Validate product
        Product product = productClient.getProductById(order.getProductId());
        if (product == null) {
            throw new InValidOrderException("Invalid product ID.");
        }
        order.setProductName(product.getProductName());

        // 3. Save order
        order.setOrderStatus(OrderStatus.PLACED);
        Order savedOrder = orderRepository.save(order);
        logger.info("✅ Order placed successfully: {}", savedOrder.getOrderReference());

        // 4. Call Payment Service via Feign
        try {
            Payment paymentReq = Payment.builder()
                    .userId(savedOrder.getCustomerId())
                    .orderId(savedOrder.getId())
                    .username(savedOrder.getUserName())
                    .method(savedOrder.getPaymentMethod())
                    .amount(savedOrder.getPrice())
                    .currencyCode("INR")
                    .emailId(savedOrder.getEmail())
                    .mobileNumber(savedOrder.getMobileNumber())
                    .status(PaymentStatus.PENDING)
                    .paymentTimestamp(LocalDateTime.now())
                    .build();

            Payment paymentResponse = paymentClient.processPayment(paymentReq);

            if (paymentResponse != null && paymentResponse.getStatus() == PaymentStatus.SUCCESS) {
                logger.info("💰 Payment successful for order: {}", savedOrder.getOrderReference());
            } else {
                savedOrder.setOrderStatus(OrderStatus.PAYMENT_FAILED);
                orderRepository.save(savedOrder);
                logger.warn("❌ Payment failed for order: {}", savedOrder.getOrderReference());
                return savedOrder;
            }
        } catch (Exception e) {
            savedOrder.setOrderStatus(OrderStatus.PAYMENT_FAILED);
            orderRepository.save(savedOrder);
            logger.error("❌ Error calling Payment Service: {}", e.getMessage());
            return savedOrder;
        }

        // 5. Publish Kafka event to Consolidation Service
        try {
            orderEventPublisher.publishOrderPlacedEvent(savedOrder);
            logger.info("📤 Kafka event published to Consolidation topic for order: {}", savedOrder.getOrderReference());
        } catch (Exception e) {
            logger.error("❌ Failed to publish Consolidation event: {}", e.getMessage());
        }

        // 6. Publish Kafka event to Notification Service
        try {
            NotificationRequest notification = new NotificationRequest();
            notification.setCustomerId(savedOrder.getCustomerId());
            notification.setOrderId(savedOrder.getId());
            notification.setOrderReference(savedOrder.getOrderReference());
            notification.setEmail(savedOrder.getEmail());
            notification.setMessage("Order placed successfully!");
            notification.setPrice(savedOrder.getPrice());
            notification.setQuantity(savedOrder.getQuantity());
            notification.setPaymentMethod(savedOrder.getPaymentMethod());
            notification.setAddress(savedOrder.getAddress());
            notification.setProductId(savedOrder.getProductId());
            notification.setProductName(savedOrder.getProductName());
            notification.setOrderType(savedOrder.getOrderType());
            notification.setType(NotificationType.EMAIL);

            orderEventPublisher.publishNotificationEvent(notification);
            logger.info("📤 Kafka event published to Notification topic for order: {}", savedOrder.getOrderReference());
        } catch (Exception e) {
            logger.warn("❌ Failed to publish notification event: {}", e.getMessage());
        }

        return savedOrder;
    }

	// Other methods like updateOrder(), deleteOrder(), etc., remain the same

	@Override
	public List<Order> processOrder(Long orderId) {
		logger.warn("processOrder is not yet implemented for orderId: {}", orderId);
		return Collections.emptyList(); // Instead of throwing an exception
	}

	@Override
	public Order updateOrder(Long orderId, Order updatedOrder) throws OrderNotFoundException {
		// Step 1: Fetch the existing order
		Order existingOrder = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

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
		logger.info("Order with ID {} updated successfully", orderId);

		return savedOrder;
	}

	@Override
	public void deleteOrder(Long orderId) throws OrderNotFoundException {
		Optional<Order> order = orderRepository.findById(orderId);
		if (order.isEmpty()) {
			logger.error("Attempted to delete non-existent order with id {}", orderId);
			throw new OrderNotFoundException("Order with id " + orderId + " not found.");
		}

		orderRepository.deleteById(orderId);
		logger.info("Successfully deleted order: {}", order.get());
	}

	@Override
	public List<Order> getOrdersForCustomer(Long customerId) throws UnauthorizedOrderAccessException {
		logger.warn("getOrdersForCustomer is not yet implemented.");
		throw new UnauthorizedOrderAccessException("getOrdersForCustomer is not yet implemented.");
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
