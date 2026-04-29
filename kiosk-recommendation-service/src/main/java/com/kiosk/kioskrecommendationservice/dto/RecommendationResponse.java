package com.kiosk.kioskrecommendationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponse {
    private String departmentCode;
    private String symptomKeyword;
    private List<DoctorScore> rankedDoctors;
    private boolean fromCache;
}
