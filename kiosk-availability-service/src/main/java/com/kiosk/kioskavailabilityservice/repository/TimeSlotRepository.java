package com.kiosk.kioskavailabilityservice.repository;

import com.kiosk.kioskavailabilityservice.model.SlotStatus;
import com.kiosk.kioskavailabilityservice.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot,String> {

    List<TimeSlot> findByScheduleIdAndStatus(String scheduleId, SlotStatus status);
    List<TimeSlot> findBySchedule_Doctor_IdAndSlotDatetimeBetween(
            String doctorId,
            LocalDateTime start,
            LocalDateTime end
    );
    List<TimeSlot> findBySchedule_Doctor_IdAndStatusAndSlotDatetimeAfter(
            String doctorId,
            SlotStatus status,
            LocalDateTime after
    );
    Optional<TimeSlot> findByIdAndStatus(String id, SlotStatus status);
    long countBySchedule_Doctor_IdAndStatus(String doctorId, SlotStatus status);
}
