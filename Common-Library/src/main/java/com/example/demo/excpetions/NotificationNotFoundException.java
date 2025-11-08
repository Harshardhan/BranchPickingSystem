package com.example.demo.excpetions;

public class NotificationNotFoundException extends ResourceNotFoundException{

	public NotificationNotFoundException(String message) {
		super(message);
	}
}
