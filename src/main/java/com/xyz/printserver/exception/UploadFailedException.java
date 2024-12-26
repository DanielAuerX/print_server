package com.xyz.printserver.exception;

public class UploadFailedException extends RuntimeException {
    public UploadFailedException(String message) {
        super(message);
    }
}
