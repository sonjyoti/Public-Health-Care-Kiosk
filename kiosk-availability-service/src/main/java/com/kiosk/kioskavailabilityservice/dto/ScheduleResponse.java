package com.kiosk.kioskavailabilityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponse {
    private String id;
    private String doctorId;
    private String doctorName;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private int slotDurationMins;
    private boolean isActive;
}
