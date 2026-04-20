package com.kiosk.kioskappointmentservice.repository;

import com.kiosk.kioskappointmentservice.model.Appointment;
import com.kiosk.kioskappointmentservice.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    Optional<Appointment> findBySessionToken(String sessionToken);
    List<Appointment> findAllByDepartmentCode(String departmentCode);
    List<Appointment> findByDoctorId(String doctorId);
    List<Appointment> findByStatus(AppointmentStatus status);
    long countByDoctorIdAndStatus(String doctorId, AppointmentStatus status);
    boolean existsBySlotId(String slotId);
}
