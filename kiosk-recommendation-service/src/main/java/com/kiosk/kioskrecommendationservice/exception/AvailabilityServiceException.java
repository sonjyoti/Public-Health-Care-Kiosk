package com.kiosk.kioskrecommendationservice.exception;

// exception for when Feign client calls availability service
public class AvailabilityServiceException extends RuntimeException {
    public AvailabilityServiceException(String message) {
        super("Availability service error: " + message);
    }
    public AvailabilityServiceException(String message, Throwable cause) {
        super("Availability service error: " + message, cause);
    }
}
