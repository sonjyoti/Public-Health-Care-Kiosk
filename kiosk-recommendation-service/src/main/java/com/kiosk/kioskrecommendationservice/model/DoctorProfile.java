package com.kiosk.kioskrecommendationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "doctor_id", unique = true, nullable = false)
    private String doctorId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "qualification", nullable = false)
    private String qualification;

    @Column(name = "years_experience", nullable = false)
    private Integer yearsExperience;

    @Column(name = "specialization", nullable = false)
    private String specialization;

    @Column(name = "department_code", nullable = false)
    private String departmentCode;

    @Column(name = "base_score")
    private double baseScore;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @PrePersist
    @PreUpdate
    protected void onSync(){
        this.lastSyncedAt = LocalDateTime.now();
    }
}
