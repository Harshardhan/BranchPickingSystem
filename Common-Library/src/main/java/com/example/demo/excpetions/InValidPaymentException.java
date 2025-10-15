package com.example.demo.excpetions;

public class InValidPaymentException extends Exception{

	public InValidPaymentException(String message) {
		super(message);
	}
}
