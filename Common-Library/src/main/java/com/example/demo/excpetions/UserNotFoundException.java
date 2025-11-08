package com.example.demo.excpetions;

public class UserNotFoundException extends ResourceNotFoundException{

	public UserNotFoundException(String message) {
		super(message);
	}
}
