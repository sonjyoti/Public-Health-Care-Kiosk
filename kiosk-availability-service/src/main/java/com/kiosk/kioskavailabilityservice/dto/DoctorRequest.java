package com.kiosk.kioskavailabilityservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Qualification is required")
    private String qualification;

    @Min(value = 0, message = "Experience cannot be negative")
    private int yearsExperience;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "Department code is required")
    private String departmentCode;
}
