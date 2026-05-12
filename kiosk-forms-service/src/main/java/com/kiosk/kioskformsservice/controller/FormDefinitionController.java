package com.kiosk.kioskformsservice.controller;

import com.kiosk.kioskformsservice.dto.FormDefinitionRequest;
import com.kiosk.kioskformsservice.dto.FormDefinitionResponse;
import com.kiosk.kioskformsservice.service.FormDefinitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms/definitions")
@RequiredArgsConstructor
public class FormDefinitionController {

    private final FormDefinitionService formDefinitionService;

    @PostMapping
    public ResponseEntity<FormDefinitionResponse> create(
            @Valid @RequestBody FormDefinitionRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(formDefinitionService.createFormDefinition(request));
    }

    @GetMapping
    public ResponseEntity<List<FormDefinitionResponse>> getAlActive() {
        return ResponseEntity.ok(
                formDefinitionService.getAllActiveForms()
        );
    }

    @GetMapping("/{formType}")
    public ResponseEntity<FormDefinitionResponse> getByFormType(
            @PathVariable String formType
    ){
        return ResponseEntity.ok(
                formDefinitionService.getByFormType(formType)
        );
    }

    @DeleteMapping("/{formType}")
    public ResponseEntity<FormDefinitionResponse> deactivate(
            @PathVariable String formType
    ){
        formDefinitionService.deactivateForm(formType);
        return ResponseEntity.ok().build();
    }
}
