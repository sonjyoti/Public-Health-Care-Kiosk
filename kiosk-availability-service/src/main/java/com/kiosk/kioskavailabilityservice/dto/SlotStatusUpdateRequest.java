package com.kiosk.kioskavailabilityservice.dto;

import com.kiosk.kioskavailabilityservice.model.SlotStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private SlotStatus status;
}
