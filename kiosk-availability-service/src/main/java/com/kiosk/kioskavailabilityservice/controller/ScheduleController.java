package com.kiosk.kioskavailabilityservice.controller;

import com.kiosk.kioskavailabilityservice.dto.ScheduleRequest;
import com.kiosk.kioskavailabilityservice.dto.ScheduleResponse;
import com.kiosk.kioskavailabilityservice.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ScheduleResponse> create(
            @Valid @RequestBody ScheduleRequest scheduleRequest){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scheduleService.createSchedule(scheduleRequest));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ScheduleResponse>> getByDoctor(
            @PathVariable String doctorId
    ){
        return ResponseEntity.ok(
                scheduleService.getSchedulesByDoctor(doctorId)
        );
    }
}
