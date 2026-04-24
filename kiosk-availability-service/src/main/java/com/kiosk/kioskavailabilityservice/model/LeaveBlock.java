package com.kiosk.kioskavailabilityservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "leave_blocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBlock {
// LeaveBlock represents a period of time when a doctor is unavailable — it's the mechanism that lets hospital staff mark a doctor as temporarily absent without deleting or modifying their permanent schedule.
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "block_date", nullable = false)
    private LocalDate blockDate;

    @Column(name = "from_time", nullable = false)
    private LocalTime fromTime;

    @Column(name = "to_time", nullable = false)
    private LocalTime toTime;

    @Column(name = "reason")
    private String reason;
}
