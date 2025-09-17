package com.example.demo.excpetions;

public class UserNotFoundException extends Exception{

	public UserNotFoundException(String message) {
		super(message);
	}
}
