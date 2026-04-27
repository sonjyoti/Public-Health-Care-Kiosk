package com.kiosk.kioskavailabilityservice.service;

import com.kiosk.kioskavailabilityservice.dto.DoctorRequest;
import com.kiosk.kioskavailabilityservice.dto.DoctorResponse;
import com.kiosk.kioskavailabilityservice.exception.DoctorNotFoundException;
import com.kiosk.kioskavailabilityservice.model.Doctor;
import com.kiosk.kioskavailabilityservice.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public DoctorResponse createDoctor(DoctorRequest doctorRequest){
        Doctor doctor = Doctor.builder()
                .name(doctorRequest.getName())
                .qualification(doctorRequest.getQualification())
                .yearsExperience(doctorRequest.getYearsExperience())
                .specialization(doctorRequest.getSpecialization())
                .departmentCode(doctorRequest.getDepartmentCode())
                .isAvailable(true)
                .isActive(true)
                .build();

        Doctor saved = doctorRepository.save(doctor);
        log.info("Doctor created: {}", saved.getId());
        return mapToResponse(saved);
    }

    public List<DoctorResponse> getAllActiveDoctors(){
        return doctorRepository.findByIsAvailableTrueAndIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DoctorResponse> getDoctorsByDepartment(String departmentCode){
        return doctorRepository
                .findByDepartmentCodeAndIsActiveTrue(departmentCode)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DoctorResponse getDoctorById(String doctorId){
        Doctor doctor = doctorRepository.findByIdAndIsActiveTrue(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        return mapToResponse(doctor);
    }

    public DoctorResponse toggleAvailability(String doctorId){
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        doctor.setAvailable(!doctor.isAvailable());
        return mapToResponse(doctorRepository.save(doctor));
    }

    public void deactivateDoctor(String doctorId){
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        doctor.setActive(false);
        doctorRepository.save(doctor);
        log.info("Doctor deactivated: {}", doctorId);
    }

    // used by recommendation service
    public long getCurrentLoad(String doctorId){
        return doctorRepository.findById(doctorId)
                .map(d -> doctorRepository
                        .findByIsAvailableTrueAndIsActiveTrue()
                        .size())
                .orElse(0)
                .longValue();
    }

    private DoctorResponse mapToResponse(Doctor doctor){
        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .qualification(doctor.getQualification())
                .yearsExperience(doctor.getYearsExperience())
                .specialization(doctor.getSpecialization())
                .departmentCode(doctor.getDepartmentCode())
                .isAvailable(doctor.isAvailable())
                .isActive(doctor.isActive())
                .build();
    }
}
