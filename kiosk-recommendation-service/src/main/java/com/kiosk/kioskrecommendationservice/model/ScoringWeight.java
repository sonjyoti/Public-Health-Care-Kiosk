package com.kiosk.kioskrecommendationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "scoring_weights")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoringWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "weight_name", unique = true, nullable = false)
    private String weightName;

    @Column(name = "weight_value", nullable = false)
    private double weightValue;

    @Column(name = "description")
    private String description;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
