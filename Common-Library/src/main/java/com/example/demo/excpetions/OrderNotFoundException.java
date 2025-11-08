package com.example.demo.excpetions;

public class OrderNotFoundException extends ResourceNotFoundException {

	public OrderNotFoundException(String message) {
		super(message);
	}
}