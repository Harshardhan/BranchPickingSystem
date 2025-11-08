package com.example.demo.excpetions;

public class UnauthorizedOrderAccessException extends BusinessException {

	public UnauthorizedOrderAccessException(String message) {
		super(message);
	}
}
