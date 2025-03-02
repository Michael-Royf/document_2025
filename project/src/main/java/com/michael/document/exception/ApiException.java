package com.michael.document.exception;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException() {
        super("Ann error occurred");
    }
}
