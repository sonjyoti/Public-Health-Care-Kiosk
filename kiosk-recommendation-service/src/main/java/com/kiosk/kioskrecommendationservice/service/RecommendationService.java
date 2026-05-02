package com.kiosk.kioskrecommendationservice.service;

import com.kiosk.kioskrecommendationservice.client.AvailabilityClient;
import com.kiosk.kioskrecommendationservice.client.DoctorResponse;
import com.kiosk.kioskrecommendationservice.dto.DoctorScore;
import com.kiosk.kioskrecommendationservice.dto.RecommendationRequest;
import com.kiosk.kioskrecommendationservice.dto.RecommendationResponse;
import com.kiosk.kioskrecommendationservice.engine.ScoringEngine;
import com.kiosk.kioskrecommendationservice.exception.AvailabilityServiceException;
import com.kiosk.kioskrecommendationservice.model.RecommendationLog;
import com.kiosk.kioskrecommendationservice.model.SymptomSpecialityMap;
import com.kiosk.kioskrecommendationservice.repository.RecommendationLogRepository;
import com.kiosk.kioskrecommendationservice.repository.SymptomSpecialityMapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final AvailabilityClient availabilityClient;
    private final ScoringEngine scoringEngine;
    private final SymptomSpecialityMapRepository symptomRepo;
    private final RecommendationLogRepository logRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long CACHE_TTL_SECONDS = 180;
    private static final int TOP_N = 3;

    public RecommendationResponse recommend(RecommendationRequest request){
        String cacheKey = buildCacheKey(
                request.getDepartmentCode(), request.getSymptomKeyword()
        );

        // check Redis cache first
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return RecommendationResponse.builder()
                    .departmentCode(request.getDepartmentCode())
                    .symptomKeyword(request.getSymptomKeyword())
                    .rankedDoctors((List<DoctorScore>) cached)
                    .fromCache(true)
                    .build();
        }

        // resolve target specialization from symptom keyword
        String targetSpec = resolveSpecialization(
                request.getSymptomKeyword(), request.getDepartmentCode()
        );

        // fetch available doctors from availability service
        List<DoctorResponse> doctors;

        try{
            doctors = availabilityClient
                    .getDoctorsByDepartment(request.getDepartmentCode());
        } catch (feign.FeignException e){
            log.error("Failed to fetch doctors from availability service: {}",
                    e.getMessage());
            throw new AvailabilityServiceException(
                    "Could not fetch doctors from department: "
                    + request.getDepartmentCode(), e
            );
        }

        if (doctors == null || doctors.isEmpty()) {
            return RecommendationResponse.builder()
                    .departmentCode(request.getDepartmentCode())
                    .symptomKeyword(request.getSymptomKeyword())
                    .rankedDoctors(Collections.emptyList())
                    .fromCache(false)
                    .build();
        }

        // fetch load for each doctor
        Map<String, Long> loadMap = new HashMap<>();
        doctors.forEach(doctor -> {
            try {
                Long load = availabilityClient.getDoctorLoad(doctor.getId());
                loadMap.put(doctor.getId(), load != null ? load : 0L);
            } catch (Exception e) {
                log.warn("Could not fetch load for doctor {}", doctor.getId());
                loadMap.put(doctor.getId(), 0L);
            }
        });

        // run scoring engine
        List<DoctorScore> ranked = scoringEngine
                .score(doctors, targetSpec, loadMap)
                .stream()
                .limit(TOP_N)
                .collect(Collectors.toList());

        // cache the result
        redisTemplate.opsForValue().set(cacheKey, ranked,
                CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        log.info("Cached recommendation for key: {}", cacheKey);

        //persist to recommendation log
        persistLogs(ranked, request.getDepartmentCode(),
                request.getSymptomKeyword());

        return RecommendationResponse.builder()
                .departmentCode(request.getDepartmentCode())
                .symptomKeyword(request.getSymptomKeyword())
                .rankedDoctors(ranked)
                .fromCache(false)
                .build();
    }

    // helpers

    private String resolveSpecialization(String symptomKeyword, String departmentCode) {
        if (symptomKeyword == null || symptomKeyword.isBlank()) {
            return departmentCode;
        }
        return symptomRepo
                .findTopBySymptomKeywordIgnoreCase(symptomKeyword)
                .map(SymptomSpecialityMap::getSpecialization)
                .orElse(departmentCode);
    }

    private void persistLogs(List<DoctorScore> ranked,
                             String departmentCode,
                             String symptomKeyword) {
        List<RecommendationLog> logs = new ArrayList<>();
        for (int i = 0; i < ranked.size(); i++) {
            DoctorScore ds =  ranked.get(i);
            logs.add(RecommendationLog.builder()
                    .doctorId(ds.getDoctorId())
                    .departmentCode(departmentCode)
                    .symptomInput(symptomKeyword)
                    .finalScore(ds.getScore())
                    .rankPosition(i + 1)
                    .build()
            );
            logRepository.saveAll(logs);
        }
    }

    private String buildCacheKey(String dept, String symptom){
        String sym = (symptom == null ||  symptom.isBlank())
                ? "general" : symptom.toLowerCase();
        return "recommend: " + dept.toLowerCase() + ":" + sym;
    }
}
