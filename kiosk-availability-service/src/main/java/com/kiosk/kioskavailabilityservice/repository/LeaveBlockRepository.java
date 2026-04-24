package com.kiosk.kioskavailabilityservice.repository;

import com.kiosk.kioskavailabilityservice.model.LeaveBlock;
import com.kiosk.kioskavailabilityservice.model.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveBlockRepository extends JpaRepository<LeaveBlock, String> {
    List<LeaveBlock> findByDoctorIdAndBlockDate(String doctorId, SlotStatus status);
    List<LeaveBlock> findByDoctorIdAndBlockDateBetween(
            String doctorId,
            LocalDate from,
            LocalDate to
    );
}
