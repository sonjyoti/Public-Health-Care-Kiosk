package com.kiosk.kioskavailabilityservice.repository;

import com.kiosk.kioskavailabilityservice.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,String> {
    List<Doctor> findByDepartmentCodeAndIsActiveTrue(String departmentCode);
    List<Doctor> findByIsAvailableTrueAndIsActiveTrue();
    List<Doctor> findBySpecializationAndIsActiveTrue(String specialization);
    List<Doctor> findByIdAndIsActiveTrue(String id);
}
