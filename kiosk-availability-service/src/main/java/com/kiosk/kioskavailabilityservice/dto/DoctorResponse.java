package com.kiosk.kioskavailabilityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {
    private String id;
    private String name;
    private String qualification;
    private int yearsExperience;
    private String specialization;
    private String departmentCode;
    private boolean isAvailable;
    private boolean isActive;
}
