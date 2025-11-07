package com.example.demo;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.excpetions.ErrorResponse;
import com.example.demo.excpetions.NotificationException;
import com.example.demo.excpetions.NotificationNotFoundException;

public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(NotificationNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotificationNotFoundException(NotificationNotFoundException ex, WebRequest request) {
		logger.warn("Notification not found: {}", ex.getMessage());
		logger.error("Unhandled exception [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

		return buildErrorResponse("Notification Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(NotificationException.class)
	public ResponseEntity<ErrorResponse>handleNotificationException(NotificationException ex , WebRequest request){
		logger.error("Notification Exception: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
		
		return buildErrorResponse("Notification exception", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
	}
	private ResponseEntity<ErrorResponse> buildErrorResponse(
	        String errorTitle, String message, HttpStatus status, WebRequest request) {
	    
	    ErrorResponse error = new ErrorResponse(
	        errorTitle,
	        message,
	        status.value(), // ✅ pass int instead of String
	        LocalDateTime.now(),
	        request.getDescription(false).replace("uri=", "")
	    );
	    
	    return new ResponseEntity<>(error, status);
	}

}
