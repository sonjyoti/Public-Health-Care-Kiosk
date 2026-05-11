package com.kiosk.kioskformsservice.repository;

import com.kiosk.kioskformsservice.model.FormDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormDefinitionRepository extends JpaRepository<FormDefinition, String> {
    List<FormDefinition> findByIsActiveTrue();

    Optional<FormDefinition> findByFormTypeAndIsActiveTrue(String formType);

}
