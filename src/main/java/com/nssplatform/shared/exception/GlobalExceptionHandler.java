package com.nssplatform.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
        return error(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return error(HttpStatus.FORBIDDEN, "Forbidden", "Access denied", req.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST, "Validation Error", message, req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {}: {}", req.getRequestURI(), ex.getMessage());
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
            "An unexpected error occurred", req.getRequestURI());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiError> handleCustomException(CustomException ex, HttpServletRequest req) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), req.getRequestURI());
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String error, String message, String path) {
        return ResponseEntity.status(status).body(new ApiError(
            Instant.now().toString(), status.value(), error, message, path
        ));
    }

    public record ApiError(String timestamp, int status, String error, String message, String path) {}
}