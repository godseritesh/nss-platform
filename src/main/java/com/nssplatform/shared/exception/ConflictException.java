package com.nssplatform.shared.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "ConflictException{" +
                "message='" + getLocalizedMessage() + '\'' +
                '}';
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}