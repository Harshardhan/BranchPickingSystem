package com.example.demo;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.demo.excpetions.AnalyticsNotFoundException;
import com.example.demo.excpetions.AnalyticsProcessingException;
import com.example.demo.excpetions.ErrorResponse;
import com.example.demo.excpetions.InvalidAnalyticsException;


@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	
    @ExceptionHandler(AnalyticsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAnalyticsNotFound(AnalyticsNotFoundException ex, WebRequest request) {
		logger.warn("Analytics not found: {}", ex.getMessage());

		return buildErrorResponse("Payment Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }


    @ExceptionHandler(InvalidAnalyticsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAnalytics(InvalidAnalyticsException ex, WebRequest request) {
		logger.warn("Invalid Analysis data: {}", ex.getMessage());
		return buildErrorResponse("Invalid Analysis", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AnalyticsProcessingException.class)
    public ResponseEntity<ErrorResponse> handleAnalyticsProcessing(AnalyticsProcessingException ex, WebRequest request){
    	logger.warn("Analytics processing data:{}", ex.getMessage());
    	return buildErrorResponse("Analytics process",ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
    
    
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
			WebRequest request) {
		String errors = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.reduce((msg1, msg2) -> msg1 + ", " + msg2).orElse("Validation failed");

		logger.warn("Validation failed: {}", errors);
		return buildErrorResponse("Validation Error", errors, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
		if (ex instanceof NoResourceFoundException) {
			return buildErrorResponse("Resource Not Found", "The requested resource could not be found.",
					HttpStatus.NOT_FOUND, request);
		}

		logger.error("Unhandled exception: ", ex);
		return buildErrorResponse("Internal Server Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
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
