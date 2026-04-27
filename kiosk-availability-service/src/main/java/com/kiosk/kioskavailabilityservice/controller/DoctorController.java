package com.kiosk.kioskavailabilityservice.controller;

import com.kiosk.kioskavailabilityservice.dto.DoctorRequest;
import com.kiosk.kioskavailabilityservice.dto.DoctorResponse;
import com.kiosk.kioskavailabilityservice.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorResponse> create(
            @Valid @RequestBody DoctorRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorService.createDoctor(request));
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAllActive(){
        return ResponseEntity.ok(doctorService.getAllActiveDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getById(@PathVariable String id){
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/department/{departmentCode}")
    public ResponseEntity<List<DoctorResponse>> getByDepartment(
            @PathVariable String departmentCode){
        return ResponseEntity.ok(
                doctorService.getDoctorsByDepartment(departmentCode)
        );
    }

    @PatchMapping("/{id}/toggle-availability")
    public ResponseEntity<DoctorResponse> toggleAvailability(
            @PathVariable String id
    ){
        return ResponseEntity.ok(doctorService.toggleAvailability(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable String id){
        doctorService.deactivateDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
