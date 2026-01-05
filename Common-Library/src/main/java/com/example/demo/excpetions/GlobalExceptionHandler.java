package com.example.demo.excpetions;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    
    @ExceptionHandler(DownstreamServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleDownstreamFailure(
            DownstreamServiceUnavailableException ex,
            WebRequest request) {

        return buildErrorResponse(
                "Service Unavailable",
                ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE,
                request
        );
    }
    
    
    // 1️⃣ Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildErrorResponse("Validation Error", errors, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        String errors = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        return buildErrorResponse("Validation Error", errors, HttpStatus.BAD_REQUEST, request);
    }

    // 2️⃣ Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse("Resource Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    // 3️⃣ Business Logic Errors
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, WebRequest request) {
        return buildErrorResponse("Business Error", ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    // 4️⃣ Method Not Supported
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return buildErrorResponse("Method Not Allowed", ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    // 5️⃣ Fallback (unhandled exceptions)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, WebRequest request) {
        if (ex instanceof ResourceNotFoundException) {
            return buildErrorResponse("Resource Not Found", "The requested resource could not be found.",
                    HttpStatus.NOT_FOUND, request);
        }
        logger.error("Unexpected exception: ", ex);
        return buildErrorResponse("Internal Server Error", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // Common builder method
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String errorTitle, String message, HttpStatus status, WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                errorTitle,
                message,
                status.value(),
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, status);
    }
    
    // 6️⃣ Access Denied (403 Forbidden)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {

        String message = "You are not authorized to perform this action. " +
                         "Please use your own account credentials or contact your administrator.";

        return buildErrorResponse(
                "Access Denied",
                message,
                HttpStatus.FORBIDDEN,
                request
        );
    }

    @ExceptionHandler(IdentityMismatchException.class)
    public ResponseEntity<ErrorResponse> handleIdentityMismatch(
            IdentityMismatchException ex, WebRequest request) {

        String message = "Invalid account operation detected. " +
                         "Please use your own account credentials.";

        return buildErrorResponse(
                "Identity Mismatch",
                message,
                HttpStatus.FORBIDDEN,
                request
        );
    }

    // 7️⃣ Authentication Issues (401 Unauthorized)
    @ExceptionHandler({ AuthenticationException.class, BadCredentialsException.class })
    public ResponseEntity<ErrorResponse> handleAuthErrors(
            Exception ex, WebRequest request) {

        return buildErrorResponse(
                "Unauthorized",
                "Authentication failed. Please check your credentials or token.",
                HttpStatus.UNAUTHORIZED,
                request
        );
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex, WebRequest request) {

        String message = "Invalid input format. Please provide valid number formats for fields like price and quantity.";

        return buildErrorResponse(
                "Invalid Request Payload",
                message,
                HttpStatus.BAD_REQUEST,
                request
        );
    }

}
