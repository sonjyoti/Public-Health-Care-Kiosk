package com.kiosk.kioskrecommendationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorScore {
    private String doctorId;
    private String doctorName;
    private String qualification;
    private String specialization;
    private String departmentCode;
    private double score;
}
