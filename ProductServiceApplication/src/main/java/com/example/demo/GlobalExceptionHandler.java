package com.example.demo;

import java.time.LocalDateTime;
import org.springframework.web.servlet.resource.NoResourceFoundException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

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

    // Generic fallback for unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        // Avoid logging for Spring's static resource errors like /metrics, /favicon.ico, etc.
        if (ex instanceof NoResourceFoundException) {
            return buildErrorResponse(
                "Resource Not Found",
                "The requested resource could not be found.",
                HttpStatus.NOT_FOUND,
                request
            );
        }

        logger.error("Unhandled Exception: ", ex);
        return buildErrorResponse("Internal Server Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String errorTitle, String message, HttpStatus status, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            errorTitle,
            message,
            status.toString(),
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, status);
    }
}
