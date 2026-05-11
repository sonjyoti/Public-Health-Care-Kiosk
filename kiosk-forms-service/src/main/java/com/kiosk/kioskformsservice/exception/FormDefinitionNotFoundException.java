package com.kiosk.kioskformsservice.exception;

public class FormDefinitionNotFoundException extends RuntimeException{
    public FormDefinitionNotFoundException(String formType){
        super("Form Definition not found for type: " + formType);
    }
}
