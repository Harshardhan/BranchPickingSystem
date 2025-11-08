package com.example.demo.excpetions;

public class UserAlreadyExistsException extends BusinessException{

	public UserAlreadyExistsException(String message) {
		super(message);
	}
}
