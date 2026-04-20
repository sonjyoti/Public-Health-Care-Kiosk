package com.kiosk.kioskappointmentservice.exception;

public class AppointmentNotFoundException extends RuntimeException{
    public AppointmentNotFoundException(String id) {
        super("Appointment not found with id: " + id);
    }
}
