package com.kiosk.kioskappointmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private String id;
    private String sessionToken;
    private String tokenNumber;
    private String departmentCode;
    private String doctorId;
    private String slotId;
    private String status;
    private LocalDateTime createdAt;
}
