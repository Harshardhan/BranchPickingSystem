package com.example.demo.excpetions;

public class UnauthorizedOrderAccessException extends Exception {

	public UnauthorizedOrderAccessException(String message) {
		super(message);
	}
}
