package com.example.demo.excpetions;

public class OrderNotFoundException extends Exception {

	public OrderNotFoundException(String message) {
		super(message);
	}
}