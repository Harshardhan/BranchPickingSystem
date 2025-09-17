package com.example.demo.excpetions;

public class PaymentNotFoundException extends Exception {

	public PaymentNotFoundException(String message) {
	super(message);
}
}