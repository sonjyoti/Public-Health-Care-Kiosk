package com.kiosk.kioskformsservice.exception;

public class InvalidFormDataException extends RuntimeException {
    public InvalidFormDataException(String message) {
        super("Invalid form data: " + message);
    }
}
