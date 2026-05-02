package com.kiosk.kioskrecommendationservice.exception;

public class ScoringWeightNotFoundException extends RuntimeException {
    public ScoringWeightNotFoundException(String weightName) {
        super("Scoring Weight Not Found: " + weightName);
    }
}
