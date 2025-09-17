package com.example.demo.excpetions;

public class PaymentAlreadyExistsException extends Exception{

	public PaymentAlreadyExistsException(String message) {
		super(message);
	}
}
