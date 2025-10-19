package com.example.demo.excpetions;

public class AnalyticsProcessingException extends Exception{

	public AnalyticsProcessingException(String message, Exception e) {
		super(message, e);
	}

	
}
