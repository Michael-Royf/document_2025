package com.michael.document.exception;

public class NotFoundException  extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}
