package com.kiosk.kioskavailabilityservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "qualification", nullable = false)
    private String qualification;

    @Column(name = "years_experience", nullable = false)
    private int yearsExperience;

    @Column(name = "specialization", nullable = false)
    private String specialization;

    @Column(name = "department_code", nullable = false)
    private String departmentCode;

    @Column(name = "is_available")
    private boolean isAvailable = true;

    @Column(name = "is_active")
    private boolean isActive = true;
}
