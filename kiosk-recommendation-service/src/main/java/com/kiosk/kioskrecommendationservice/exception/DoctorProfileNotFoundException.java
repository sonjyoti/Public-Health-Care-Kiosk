package com.kiosk.kioskrecommendationservice.exception;

public class DoctorProfileNotFoundException extends RuntimeException {
    public DoctorProfileNotFoundException(String doctorId) {
        super("Doctor Profile Not Found for Doctor ID: " + doctorId);
    }
}
