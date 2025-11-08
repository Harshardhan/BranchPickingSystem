package com.example.demo.excpetions;

public class PaymentAlreadyExistsException extends BusinessException{

	public PaymentAlreadyExistsException(String message) {
		super(message);
	}
}
