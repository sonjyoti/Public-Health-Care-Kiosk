package com.kiosk.kioskappointmentservice.repository;

import com.kiosk.kioskappointmentservice.model.AppointmentAudit;
import com.kiosk.kioskappointmentservice.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentAuditRepository extends JpaRepository<AppointmentAudit,Long> {
    List<AppointmentAudit> findByAppointmentIdOrderByChangedAtAsc(String appointmentId);
}
