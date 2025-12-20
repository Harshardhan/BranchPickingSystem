package com.example.demo;

public interface OrderEventPublisher {

	void publishOrder(Order order);
	
	void publishNotification(NotificationRequest notification);
	
	void publishPayment(Payment payment);
	
	void publishAnalytics(Analytics analytics);
}
