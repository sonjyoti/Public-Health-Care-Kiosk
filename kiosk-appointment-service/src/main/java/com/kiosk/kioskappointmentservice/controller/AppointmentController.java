package com.kiosk.kioskappointmentservice.controller;

import com.kiosk.kioskappointmentservice.dto.AppointmentResponse;
import com.kiosk.kioskappointmentservice.dto.BookingRequest;
import com.kiosk.kioskappointmentservice.dto.StatusUpdateRequest;
import com.kiosk.kioskappointmentservice.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<AppointmentResponse> book(
            @Valid @RequestBody BookingRequest request
    ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(appointmentService.bookAppointment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getById(
            @PathVariable String id
    ){
        return ResponseEntity
                .ok(appointmentService.getById(id));
    }

    @GetMapping("/session/{sessionToken}")
    public ResponseEntity<AppointmentResponse> getBySession(
            @PathVariable String sessionToken
    ){
        return ResponseEntity
                .ok(appointmentService.getBySessionToken(sessionToken));

    }

    @GetMapping("/department/{departmentCode}")
    public ResponseEntity<List<AppointmentResponse>> getByDepartment(
            @PathVariable String departmentCode
    ){
        return ResponseEntity
                .ok(appointmentService.getByDepartment(departmentCode));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody StatusUpdateRequest request
            ){
        return ResponseEntity
                .ok(appointmentService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable String id
    ){
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
