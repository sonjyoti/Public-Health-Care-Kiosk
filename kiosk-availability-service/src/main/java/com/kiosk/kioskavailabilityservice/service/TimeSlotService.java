package com.kiosk.kioskavailabilityservice.service;

import com.kiosk.kioskavailabilityservice.dto.LeaveBlockRequest;
import com.kiosk.kioskavailabilityservice.dto.SlotStatusUpdateRequest;
import com.kiosk.kioskavailabilityservice.dto.TimeSlotResponse;
import com.kiosk.kioskavailabilityservice.exception.SlotNotAvailableException;
import com.kiosk.kioskavailabilityservice.model.Doctor;
import com.kiosk.kioskavailabilityservice.model.LeaveBlock;
import com.kiosk.kioskavailabilityservice.model.SlotStatus;
import com.kiosk.kioskavailabilityservice.model.TimeSlot;
import com.kiosk.kioskavailabilityservice.repository.DoctorRepository;
import com.kiosk.kioskavailabilityservice.repository.LeaveBlockRepository;
import com.kiosk.kioskavailabilityservice.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeSlotService {
    private final TimeSlotRepository timeSlotRepository;
    private final LeaveBlockRepository leaveBlockRepository;
    private final DoctorRepository doctorRepository;

    public List<TimeSlotResponse> getOpenSlotsForDoctor(String doctorId){
        return timeSlotRepository
                .findBySchedule_Doctor_IdAndStatusAndSlotDatetimeAfter(
                        doctorId, SlotStatus.OPEN, LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public boolean isSlotOpen(String slotId){
        return timeSlotRepository
                .findByIdAndStatus(slotId, SlotStatus.OPEN)
                .isPresent();
    }

    public TimeSlotResponse updateSlotStatus(String slotId, SlotStatusUpdateRequest request){
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotAvailableException(slotId));
        slot.setStatus(request.getStatus());
        return mapToResponse(timeSlotRepository.save(slot));
    }

    public void blockLeave(LeaveBlockRequest request){
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new SlotNotAvailableException(request.getDoctorId()));

        //save leave block record
        LeaveBlock block = LeaveBlock.builder()
                .doctor(doctor)
                .blockDate(request.getBlockDate())
                .fromTime(request.getFromTime())
                .toTime(request.getToTime())
                .reason((request.getReason()))
                .build();

        leaveBlockRepository.save(block);

        //mark all affected slots as BLOCKED
        LocalDateTime from = LocalDateTime.of(request.getBlockDate(), request.getFromTime());
        LocalDateTime to = LocalDateTime.of(request.getBlockDate(), request.getToTime());

        List<TimeSlot> affected = timeSlotRepository
                .findBySchedule_Doctor_IdAndSlotDatetimeBetween(
                        request.getDoctorId(), from, to
                );

        affected.forEach(slot -> slot.setStatus(SlotStatus.BLOCKED));
        timeSlotRepository.saveAll(affected);

        log.info("Blocked {} slots for doctor {} on {}",
                affected.size(), request.getDoctorId(), request.getBlockDate());
    }

    public long getDoctorLoad(String doctorId){
        return timeSlotRepository.countBySchedule_Doctor_IdAndStatus(
                doctorId, SlotStatus.BOOKED
        );
    }

    private TimeSlotResponse mapToResponse(TimeSlot timeSlot){
        Doctor doctor = timeSlot.getSchedule().getDoctor();
        return TimeSlotResponse.builder()
                .id(timeSlot.getId())
                .doctorId(doctor.getId())
                .doctorName(doctor.getName())
                .slotDatetime(timeSlot.getSlotDatetime())
                .status(timeSlot.getStatus().name())
                .queuePosition(timeSlot.getQueuePosition())
                .build();
    }
}
