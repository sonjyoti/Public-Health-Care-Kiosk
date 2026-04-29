package com.kiosk.kioskrecommendationservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    @NotBlank(message = "Department code is required")
    private String departmentCode;

    private String symptomKeyword;
}
