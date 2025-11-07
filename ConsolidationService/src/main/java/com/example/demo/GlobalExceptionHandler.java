package com.example.demo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.excpetions.ErrorResponse;
import com.example.demo.excpetions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handling ConsolidationNotFoundException
	@ExceptionHandler(ConsolidationNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleConsolidationNotFoundException(ConsolidationNotFoundException ex, WebRequest request) {
		logger.warn("Consolidation not found: {}", ex.getMessage());
		logger.error("Unhandled exception [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

		return buildErrorResponse("Consolidation Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, request);
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
