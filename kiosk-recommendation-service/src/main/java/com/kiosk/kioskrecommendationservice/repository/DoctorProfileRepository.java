package com.kiosk.kioskrecommendationservice.repository;

import com.kiosk.kioskrecommendationservice.model.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile,String> {

    List<DoctorProfile> findByDepartmentCode(String departmentCode);

    List<DoctorProfile> findBySpecialization(String specialization);

    List<DoctorProfile> findByDoctorId(String doctorId);
}
