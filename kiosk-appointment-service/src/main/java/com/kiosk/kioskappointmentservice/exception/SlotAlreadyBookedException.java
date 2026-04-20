package com.kiosk.kioskappointmentservice.exception;

public class SlotAlreadyBookedException extends RuntimeException {
    public SlotAlreadyBookedException(String slotId) {
        super("Slot already booked: " + slotId);
    }
}
