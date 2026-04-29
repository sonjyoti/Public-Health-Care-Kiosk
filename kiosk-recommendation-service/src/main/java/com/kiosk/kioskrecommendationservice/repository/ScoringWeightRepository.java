package com.kiosk.kioskrecommendationservice.repository;

import com.kiosk.kioskrecommendationservice.model.ScoringWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoringWeightRepository extends JpaRepository<ScoringWeight,String> {
    Optional<ScoringWeight> findByWeightName(String weightName);
}
