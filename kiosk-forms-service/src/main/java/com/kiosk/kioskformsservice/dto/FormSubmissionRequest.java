package com.kiosk.kioskformsservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormSubmissionRequest {

    @NotBlank(message = "Form type is required")
    private String formType;

    private String sessionToken;

    @NotBlank(message = "Form data is required")
    private String formData;
}
