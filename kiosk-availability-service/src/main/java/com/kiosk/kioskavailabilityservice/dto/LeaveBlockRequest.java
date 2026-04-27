package com.kiosk.kioskavailabilityservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBlockRequest {

    @NotBlank(message = "Doctor ID is required")
    private String doctorId;

    @NotNull(message = "Block date is required")
    private LocalDate blockDate;

    @NotNull(message = "From time is required")
    private LocalTime fromTime;

    @NotNull(message = "To time is required")
    private LocalTime toTime;

    private String reason;
}
