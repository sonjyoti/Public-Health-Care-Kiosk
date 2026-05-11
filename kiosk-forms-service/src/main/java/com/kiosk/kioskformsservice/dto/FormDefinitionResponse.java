package com.kiosk.kioskformsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormDefinitionResponse {

    private String formType;
    private String displayName;
    private String displayNameHi;
    private String displayNameAs;
    private String fieldSchema;
    private boolean isActive;
}
