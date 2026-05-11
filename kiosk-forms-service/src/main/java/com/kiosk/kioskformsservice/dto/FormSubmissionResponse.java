package com.kiosk.kioskformsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormSubmissionResponse {
    private String id;
    private String formType;
    private String sessionToken;
    private String formData;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime processedAt;
}
