package com.example.demo.excpetions;

public class InValidPaymentException extends BusinessException{

	public InValidPaymentException(String message) {
		super(message);
	}
}
