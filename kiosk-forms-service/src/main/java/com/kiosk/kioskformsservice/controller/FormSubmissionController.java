package com.kiosk.kioskformsservice.controller;

import com.kiosk.kioskformsservice.dto.FormSubmissionRequest;
import com.kiosk.kioskformsservice.dto.FormSubmissionResponse;
import com.kiosk.kioskformsservice.dto.StatusUpdateRequest;
import com.kiosk.kioskformsservice.model.FormStatus;
import com.kiosk.kioskformsservice.service.FormSubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms/submissions")
@RequiredArgsConstructor
public class FormSubmissionController {

    private final FormSubmissionService formSubmissionService;

    @PostMapping(consumes = "application/json",
                produces = "application/json")
    public ResponseEntity<FormSubmissionResponse> submit(
            @Valid @RequestBody FormSubmissionRequest request
            ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(formSubmissionService.submit(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormSubmissionResponse> getById(
            @PathVariable String id
    ){
        return ResponseEntity.ok(formSubmissionService.getById(id));
    }

    @GetMapping("/session/{sessionToken}")
    public ResponseEntity<List<FormSubmissionResponse>> getBySession(
            @PathVariable String sessionToken
    ){
        return ResponseEntity.ok(
                formSubmissionService.getSessionToken(sessionToken)
        );
    }

    @GetMapping("/type/{formType}")
    public ResponseEntity<List<FormSubmissionResponse>> getByFormType(
            @PathVariable String formType
    ){
        return ResponseEntity.ok(
                formSubmissionService.getByFormType(formType)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FormSubmissionResponse>> getByStatus(
            @PathVariable FormStatus status
    ){
        return ResponseEntity.ok(
                formSubmissionService.getByStatus(status)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<FormSubmissionResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody StatusUpdateRequest request
            ){
        return ResponseEntity.ok(
                formSubmissionService.updateStatus(id, request)
        );
    }
}
