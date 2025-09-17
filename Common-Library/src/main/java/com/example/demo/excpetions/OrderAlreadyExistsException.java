package com.example.demo.excpetions;

public class OrderAlreadyExistsException extends Exception {

	public OrderAlreadyExistsException(String message) {
		super(message);
	}
}
