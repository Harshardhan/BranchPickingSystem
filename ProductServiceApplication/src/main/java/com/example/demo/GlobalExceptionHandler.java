package com.example.demo;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.demo.excpetions.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex, WebRequest request) {
        return buildErrorResponse("Product Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleProductAlreadyExistsException(ProductAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse("Duplicate Product", ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(InValidProductException.class)
    public ResponseEntity<ErrorResponse> handleInValidProductException(InValidProductException ex, WebRequest request) {
        return buildErrorResponse("Invalid Product", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return buildErrorResponse("Method Not Allowed", ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        if (ex instanceof NoResourceFoundException) {
            return buildErrorResponse("Resource Not Found",
                "The requested resource could not be found.",
                HttpStatus.NOT_FOUND,
                request);
        }

        logger.error("Unhandled exception [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return buildErrorResponse(
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            HttpStatus.INTERNAL_SERVER_ERROR,
            request
        );
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
