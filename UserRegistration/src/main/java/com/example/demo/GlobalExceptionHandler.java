package com.example.demo;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;

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
import com.example.demo.excpetions.InValidUserException;
import com.example.demo.excpetions.UserAlreadyExistsException;
import com.example.demo.excpetions.UserNotFoundException;

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

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
		logger.warn("User not found: {}", ex.getMessage());
		return buildErrorResponse("User Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex,
			WebRequest request) {
		logger.warn("Duplicate user registration attempt: {}", ex.getMessage());
		return buildErrorResponse("Duplicate User", ex.getMessage(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(InValidUserException.class)
	public ResponseEntity<ErrorResponse> handleInValidUserException(InValidUserException ex, WebRequest request) {
		logger.warn("Invalid user data: {}", ex.getMessage());
		return buildErrorResponse("Invalid User", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
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
