package com.kiosk.kioskavailabilityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotResponse {
    private String id;
    private String doctorId;
    private String doctorName;
    private LocalDateTime slotDatetime;
    private String status;
    private int queuePosition;
}
