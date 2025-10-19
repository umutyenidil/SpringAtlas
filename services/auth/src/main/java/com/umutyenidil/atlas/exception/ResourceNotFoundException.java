package com.umutyenidil.atlas.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String messageCode) {
        super(messageCode);
    }
}
