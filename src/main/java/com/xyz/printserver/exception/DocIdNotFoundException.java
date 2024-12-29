package com.xyz.printserver.exception;

public class DocIdNotFoundException extends RuntimeException {
    public DocIdNotFoundException(String message) {
        super(message);
    }
}
