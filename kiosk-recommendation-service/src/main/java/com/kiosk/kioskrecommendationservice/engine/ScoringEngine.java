package com.kiosk.kioskrecommendationservice.engine;

import com.kiosk.kioskrecommendationservice.client.DoctorResponse;
import com.kiosk.kioskrecommendationservice.dto.DoctorScore;
import com.kiosk.kioskrecommendationservice.model.ScoringWeight;
import com.kiosk.kioskrecommendationservice.repository.ScoringWeightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoringEngine {

    private final ScoringWeightRepository weightRepository;

    //default weights if db has none
    private static final double DEFAULT_W_QUALIFICATION = 0.25;
    private static final double DEFAULT_W_EXPERIENCE = 0.30;
    private static final double DEFAULT_W_SPECIALIZATION = 0.30;
    private static final double DEFAULT_W_LOAD = 0.15;

    public List<DoctorScore> score(List<DoctorResponse> doctors,
                                    String targetSpecialization,
                                    Map<String, Long> loadMap){
        double wQual = getWeight("W_QUALIFICATION", DEFAULT_W_QUALIFICATION);
        double wExp = getWeight("W_EXPERIENCE", DEFAULT_W_EXPERIENCE);
        double wSpec = getWeight("W_SPECIALIZATION", DEFAULT_W_SPECIALIZATION);
        double wLoad = getWeight("W_LOAD", DEFAULT_W_LOAD);

        return doctors.stream()
                .map(doctor -> {
                    double qual = normalizeQualification(
                            doctor.getQualification()
                    );
                    double exp = normalizeExperience(
                            doctor.getYearExperience()
                    );
                    double spec = specializationMatch(
                            doctor.getSpecialization(), targetSpecialization
                    );
                    double load = 1.0 - normalizeLoad(
                            loadMap.getOrDefault(doctor.getId(), 0L)
                    );

                    double total = (wQual * qual)
                            + (wExp * exp)
                            + (wSpec * spec)
                            + (wLoad * load);

                    log.debug("Doctor {} score: qual={} exp={} spec={} load={} total={}",
                            doctor.getName(), qual, exp, spec, load, total);

                    return DoctorScore.builder()
                            .doctorId(doctor.getId())
                            .doctorName(doctor.getName())
                            .qualification(doctor.getQualification())
                            .departmentCode(doctor.getDepartmentCode())
                            .score(total)
                            .build();
                })
                .sorted(Comparator.comparingDouble(
                        DoctorScore::getScore
                ).reversed())
                .collect(Collectors.toList());
    }

    // normalizers
    private double normalizeQualification(String qualification){
        if (qualification == null) return 0.4;
        return switch (qualification.toUpperCase()){
            case "DM", "MCH" -> 1.0;
            case "MD", "MS"  -> 0.85;
            case "DNB"       -> 0.75;
            case "MBBS"      -> 0.60;
            default          -> 0.40;
        };
    }

    private double normalizeExperience(int years){
        // cap at 20 years = 1.0
        return Math.min(years / 20.0, 1.0);
    }

    private double specializationMatch(String doctorSpec,
                                       String targetSpec){
        if (doctorSpec == null || targetSpec == null) return 0.0;
        return doctorSpec.equalsIgnoreCase(targetSpec) ? 1.0 : 0.0;
    }

    private double normalizeLoad(long queueSize){
        // cap at 20 booked patients = full load = 1.0
        return Math.min(queueSize / 30.0, 1.0);
    }

    private double getWeight(String name, double defaultValue){
        return weightRepository.findByWeightName(name)
                .map(ScoringWeight::getWeightValue)
                .orElse(defaultValue);
    }
}
