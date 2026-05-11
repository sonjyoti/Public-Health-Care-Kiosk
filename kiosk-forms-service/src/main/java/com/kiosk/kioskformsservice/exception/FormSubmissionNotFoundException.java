package com.kiosk.kioskformsservice.exception;

public class FormSubmissionNotFoundException extends RuntimeException {
    public FormSubmissionNotFoundException(String id) {
        super("Form Submission not found for id: " + id);
    }
}
