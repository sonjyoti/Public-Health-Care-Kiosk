package com.kiosk.kioskformsservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormDefinitionRequest {

    @NotBlank(message = "Form type is required")
    private String formType;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private String displayNameHi;
    private String displayNameAs;

    @NotBlank(message = "Field schema is required")
    private String fieldSchema;
}
