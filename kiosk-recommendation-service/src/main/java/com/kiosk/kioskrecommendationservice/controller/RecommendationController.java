package com.kiosk.kioskrecommendationservice.controller;

import com.kiosk.kioskrecommendationservice.dto.RecommendationRequest;
import com.kiosk.kioskrecommendationservice.dto.RecommendationResponse;
import com.kiosk.kioskrecommendationservice.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationResponse> recommend(
            @Valid @RequestBody RecommendationRequest request
            ){
        log.info("Recommendation request: dept={}, symptom={}",
                request.getDepartmentCode(), request.getSymptomKeyword());

        return ResponseEntity.ok(recommendationService.recommend(request));
    }
}
