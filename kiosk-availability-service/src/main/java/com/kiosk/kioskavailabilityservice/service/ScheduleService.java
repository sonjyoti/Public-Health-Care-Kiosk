package com.kiosk.kioskavailabilityservice.service;

import com.kiosk.kioskavailabilityservice.dto.ScheduleRequest;
import com.kiosk.kioskavailabilityservice.dto.ScheduleResponse;
import com.kiosk.kioskavailabilityservice.exception.DoctorNotFoundException;
import com.kiosk.kioskavailabilityservice.model.Doctor;
import com.kiosk.kioskavailabilityservice.model.Schedule;
import com.kiosk.kioskavailabilityservice.model.SlotStatus;
import com.kiosk.kioskavailabilityservice.model.TimeSlot;
import com.kiosk.kioskavailabilityservice.repository.DoctorRepository;
import com.kiosk.kioskavailabilityservice.repository.ScheduleRepository;
import com.kiosk.kioskavailabilityservice.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final TimeSlotRepository timeSlotRepository;

    public ScheduleResponse createSchedule(ScheduleRequest scheduleRequest){
        Doctor doctor = doctorRepository.findById(scheduleRequest.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException(scheduleRequest.getDoctorId()));

        Schedule schedule = Schedule.builder()
                .doctor(doctor)
                .dateOfWeek(scheduleRequest.getDayOfWeek().toUpperCase())
                .startTime(scheduleRequest.getStartTime())
                .endTime(scheduleRequest.getEndTime())
                .slotDurationMins(scheduleRequest.getSlotDurationMins())
                .isActive(true)
                .build();

        Schedule saved = scheduleRepository.save(schedule);
        log.info("Schedule created for doctor: {}", doctor.getId());

        //auto-generated slots for next 7 days
        generateSlotsForSchedule(saved);

        return mapToResponse(saved, doctor);
    }

    public List<ScheduleResponse> getSchedulesByDoctor(String doctorId){
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        return scheduleRepository.findByDoctorIdAndIsActiveTrue(doctorId)
                .stream()
                .map(s -> mapToResponse(s, doctor))
                .collect(Collectors.toList());
    }

    // generates time slots  bby expanding the schedule window
    private void generateSlotsForSchedule(Schedule schedule){
        LocalDate today = LocalDate.now();
        List<TimeSlot> slots =  new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            String dayName = date.getDayOfWeek().name();

            if (dayName.equalsIgnoreCase(schedule.getDateOfWeek())) continue;

            LocalTime cursor = schedule.getStartTime();
            int position = 1;

            while (!cursor.plusMinutes(schedule.getSlotDurationMins())
                    .isAfter(schedule.getEndTime())){
                slots.add(TimeSlot.builder()
                        .schedule(schedule)
                        .slotDatetime(LocalDateTime.of(date, cursor))
                        .status(SlotStatus.OPEN)
                        .queuePosition(position++)
                        .build());
                cursor = cursor.plusMinutes(schedule.getSlotDurationMins());
            }
        }
        timeSlotRepository.saveAll(slots);
        log.info("Generated {} slots for schedule: {}", slots.size(), schedule.getId());
    }

    private ScheduleResponse mapToResponse(Schedule schedule, Doctor doctor){
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .doctorId(doctor.getId())
                .doctorName(doctor.getName())
                .dayOfWeek(schedule.getDateOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .slotDurationMins(schedule.getSlotDurationMins())
                .isActive(schedule.isActive())
                .build();
    }
}
