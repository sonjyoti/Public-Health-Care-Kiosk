package com.kiosk.kioskavailabilityservice.exception;

public class SlotNotAvailableException extends RuntimeException{
    public SlotNotAvailableException(String slotId) {
        super("Slot is not available" + slotId);
    }
}
