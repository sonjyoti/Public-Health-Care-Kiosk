package com.kiosk.kioskavailabilityservice.model;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.TimeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "time_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",  nullable = false)
    private SlotStatus status;

    @Column(name = "queue_position")
    private int queuePosition;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = SlotStatus.OPEN;
        }
    }
}
