package com.kiosk.kioskappointmentservice.service;

import com.kiosk.kioskappointmentservice.dto.AppointmentResponse;
import com.kiosk.kioskappointmentservice.dto.BookingRequest;
import com.kiosk.kioskappointmentservice.dto.StatusUpdateRequest;
import com.kiosk.kioskappointmentservice.exception.AppointmentNotFoundException;
import com.kiosk.kioskappointmentservice.exception.SlotAlreadyBookedException;
import com.kiosk.kioskappointmentservice.model.Appointment;
import com.kiosk.kioskappointmentservice.model.AppointmentAudit;
import com.kiosk.kioskappointmentservice.model.AppointmentStatus;
import com.kiosk.kioskappointmentservice.repository.AppointmentAuditRepository;
import com.kiosk.kioskappointmentservice.repository.AppointmentRepository;
import com.kiosk.kioskappointmentservice.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DepartmentRepository departmentRepository;
    private final AppointmentAuditRepository appointmentAuditRepository;

    public AppointmentResponse bookAppointment(BookingRequest bookingRequest) {

        // generate session token if not provided (new kiosk session)
        if(bookingRequest.getSessionToken() == null || bookingRequest.getSessionToken().isBlank()) {
            bookingRequest.setSessionToken(UUID.randomUUID().toString());
        }

        // validate department exists
        departmentRepository.findByCode(bookingRequest.getDepartmentCode())
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "Department not found: " + bookingRequest.getDepartmentCode()
                ));

        // check if slot is already taken
        if(appointmentRepository.existsBySlotId(bookingRequest.getSlotId())) {
            throw new SlotAlreadyBookedException("Slot already booked: " + bookingRequest.getSlotId());
        }

        // generate token number eg. CARDIO-0009
        String tokenNumber = generateTokenNumber(bookingRequest.getDepartmentCode());

        Appointment appointment = Appointment.builder()
                .sessionToken(bookingRequest.getSessionToken())
                .departmentCode(bookingRequest.getDepartmentCode())
                .doctorId(bookingRequest.getDoctorId())
                .slotId(bookingRequest.getSlotId())
                .tokenNumber(tokenNumber)
                .status(AppointmentStatus.BOOKED)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment booked: token={}, dept={} ", tokenNumber, bookingRequest.getDepartmentCode());

        //write initial audit entry
        saveAudit(saved.getId(), null, AppointmentStatus.BOOKED);

        return mapToResponse(saved);
    }

    public AppointmentResponse getBySessionToken(String sessionToken) {
        Appointment appointment = appointmentRepository
                .findBySessionToken(sessionToken)
                .orElseThrow(() -> new AppointmentNotFoundException(sessionToken));
        return mapToResponse(appointment);
    }

    public AppointmentResponse getById(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
        return mapToResponse(appointment);
    }

    public List<AppointmentResponse> getByDepartment(String departmentCode) {
        return appointmentRepository.findAllByDepartmentCode(departmentCode)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse updateStatus(String id, StatusUpdateRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));

        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(request.getStatus());

        Appointment updated = appointmentRepository.save(appointment);
        log.info("Appointment {} status changed: {} -> {}",
                id, oldStatus, request.getStatus());

        saveAudit(id, oldStatus, request.getStatus());

        return mapToResponse(updated);

    }

    public void cancelAppointment(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));

        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    // private helpers
    private String generateTokenNumber(String departmentCode) {
        long count = appointmentRepository
                .countByDoctorIdAndStatus(departmentCode, AppointmentStatus.BOOKED);
        return departmentCode.toUpperCase() + "-" + String.format("%03d", count+1);
    }

    private void saveAudit(String appointmentId, AppointmentStatus oldStatus, AppointmentStatus newStatus) {
        AppointmentAudit audit =  AppointmentAudit.builder()
                .appointmentId(appointmentId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .build();
        appointmentAuditRepository.save(audit);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .tokenNumber(appointment.getTokenNumber())
                .sessionToken(appointment.getSessionToken())
                .departmentCode(appointment.getDepartmentCode())
                .doctorId(appointment.getDoctorId())
                .slotId(appointment.getSlotId())
                .status(appointment.getStatus().name())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
