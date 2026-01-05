package com.example.demo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
public class NoOpOrderEventPublisher implements OrderEventPublisher {


    public void publish(Order order) {
        // intentionally empty
    }

	@Override
	public void publishOrder(Order order) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publishNotification(NotificationRequest notification) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publishPayment(Payment payment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publishAnalytics(Analytics analytics) {
		// TODO Auto-generated method stub
		
	}
}
