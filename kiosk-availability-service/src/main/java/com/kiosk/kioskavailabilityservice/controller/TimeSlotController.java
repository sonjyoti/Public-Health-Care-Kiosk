package com.kiosk.kioskavailabilityservice.controller;

import com.kiosk.kioskavailabilityservice.dto.LeaveBlockRequest;
import com.kiosk.kioskavailabilityservice.dto.SlotStatusUpdateRequest;
import com.kiosk.kioskavailabilityservice.dto.TimeSlotResponse;
import com.kiosk.kioskavailabilityservice.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability/slots")
@RequiredArgsConstructor
public class TimeSlotController {
    private final TimeSlotService timeSlotService;

    @GetMapping("/doctor/{doctorId}/open")
    public ResponseEntity<List<TimeSlotResponse>> getOpenSlots(
            @PathVariable String doctorId
    ){
        return ResponseEntity.ok(
                timeSlotService.getOpenSlotsForDoctor(doctorId)
        );
    }

    @GetMapping("/{slotId}/is-open")
    public ResponseEntity<Boolean> isSlotOpen(
            @PathVariable String slotId
    ){
        return ResponseEntity.ok(timeSlotService.isSlotOpen(slotId));
    }

    @PatchMapping("/{slotId}/status")
    public ResponseEntity<TimeSlotResponse> updateStatus(
            @PathVariable String slotId,
            @Valid @RequestBody SlotStatusUpdateRequest request
            ){
        return ResponseEntity.ok(
                timeSlotService.updateSlotStatus(slotId, request)
        );
    }

    @PostMapping("/block-leave")
    public ResponseEntity<Void> blockLeave(
            @Valid @RequestBody LeaveBlockRequest request
    ){
        timeSlotService.blockLeave(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctor/{doctorId}/load")
    public ResponseEntity<Long> getDoctorLoad(
            @PathVariable String doctorId
    ){
        return ResponseEntity.ok(timeSlotService.getDoctorLoad(doctorId));
    }
}
