package com.michael.document.exception.payload;

public class ExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ExistsException(String message) {
        super(message);
    }

    public ExistsException() {
        super("An error occurred");
    }

}
