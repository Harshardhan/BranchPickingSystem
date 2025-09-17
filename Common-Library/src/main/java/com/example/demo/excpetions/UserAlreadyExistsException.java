package com.example.demo.excpetions;

public class UserAlreadyExistsException extends Exception{

	public UserAlreadyExistsException(String message) {
		super(message);
	}
}
