package com.kiosk.kioskrecommendationservice.repository;

import com.kiosk.kioskrecommendationservice.model.RecommendationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationLogRepository extends JpaRepository<RecommendationLog,String> {
    List<RecommendationLog> findByDoctorIdOrderByRecommendedAtDesc(String doctorId);
}
