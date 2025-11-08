package com.example.demo.excpetions;

public class OrderAlreadyExistsException extends BusinessException {

	public OrderAlreadyExistsException(String message) {
		super(message);
	}
}
