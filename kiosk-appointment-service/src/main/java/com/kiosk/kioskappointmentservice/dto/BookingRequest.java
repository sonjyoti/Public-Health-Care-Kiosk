package com.kiosk.kioskappointmentservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @NotBlank(message = "Department code is required")
    private String departmentCode;

    @NotBlank(message = "Doctor ID is required")
    private String doctorId;

    @NotBlank(message = "Slot ID is required")
    private String slotId;

    private String sessionToken;
}
