package com.kiosk.kioskrecommendationservice.repository;

import com.kiosk.kioskrecommendationservice.model.SymptomSpecialityMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SymptomSpecialityMapRepository extends JpaRepository<SymptomSpecialityMap, String> {

    List<SymptomSpecialityMap> findBySymptomKeywordContainingIgnoreCase(String keyword);

    Optional<SymptomSpecialityMap> findTopBySymptomKeywordIgnoreCase(String keyword);
}
