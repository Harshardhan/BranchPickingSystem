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

import com.example.demo.excpetions.ErrorResponse;
import com.example.demo.excpetions.InValidPaymentException;
import com.example.demo.excpetions.PaymentNotFoundException;
import com.example.demo.excpetions.PaymentAlreadyExistsException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex,
			WebRequest request) {
		String errors = ex.getConstraintViolations().stream()
				.map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
				.reduce((msg1, msg2) -> msg1 + ", " + msg2).orElse("Validation failed");

		logger.warn("Constraint violation: {}", errors);
		return buildErrorResponse("Validation Error", errors, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(PaymentNotFoundException.class)
	public ResponseEntity<ErrorResponse> handlePaymentrNotFoundException(PaymentNotFoundException ex, WebRequest request) {
		logger.warn("Payment not found: {}", ex.getMessage());
		return buildErrorResponse("Payment Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, request);
	}
	
	@ExceptionHandler(PaymentAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handlePaymentAlreadyExistsException(PaymentAlreadyExistsException ex,
			WebRequest request) {
		logger.warn("Duplicate payments  attempt: {}", ex.getMessage());
		return buildErrorResponse("Duplicate payment", ex.getMessage(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(InValidPaymentException.class)
	public ResponseEntity<ErrorResponse> handleInValidPaymentException(InValidPaymentException ex, WebRequest request) {
		logger.warn("Invalid payment data: {}", ex.getMessage());
		return buildErrorResponse("Invalid payment", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
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

	private ResponseEntity<ErrorResponse> buildErrorResponse(String errorTitle, String message, HttpStatus status,
			WebRequest request) {
		ErrorResponse error = new ErrorResponse(errorTitle, message, status.toString(), LocalDateTime.now(),
				request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(error, status);
	}


}
