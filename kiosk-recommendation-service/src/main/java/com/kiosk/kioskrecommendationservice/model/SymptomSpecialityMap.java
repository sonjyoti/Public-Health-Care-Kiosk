package com.kiosk.kioskrecommendationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "symptom_speciality_map")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SymptomSpecialityMap {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "symptom_keyword", nullable = false)
    private String symptomKeyword;

    @Column(name = "specialization", nullable = false)
    private String specialization;

    @Column(name = "match_weight", nullable = false)
    private double matchWeight;
}
