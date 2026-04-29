package com.kiosk.kioskrecommendationservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// local copy of the availability service's response DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private String id;
    private String name;
    private String qualification;
    private int yearExperience;
    private String specialization;
    private String departmentCode;
    private boolean isAvailable;
    private boolean isActive;
}
