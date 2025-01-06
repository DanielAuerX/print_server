package com.xyz.printserver.exception;

public class InvalidPrintSettings extends RuntimeException {
    public InvalidPrintSettings(String message) {
        super(message);
    }
}
