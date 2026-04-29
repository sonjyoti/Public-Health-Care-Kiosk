package com.kiosk.kioskrecommendationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "doctor_id", nullable = false)
    private String doctorId;

    @Column(name = "department_code")
    private String departmentCode;

    @Column(name = "symptom_input")
    private String symptomInput;

    @Column(name = "final_score")
    private double finalScore;

    @Column(name = "rank_position")
    private String rankPosition;

    @Column(name = "recommended_at")
    private LocalDateTime recommendedAt;

    @PrePersist
    protected void onCreate(){
        this.recommendedAt = LocalDateTime.now();
    }
}
