package com.kiosk.kioskformsservice.repository;

import com.kiosk.kioskformsservice.model.FormStatus;
import com.kiosk.kioskformsservice.model.FormSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormSubmissionRepository extends JpaRepository<FormSubmission, String> {
    List<FormSubmission> findByFormType(String formType);

    List<FormSubmission> findBySessionToken(String sessionToken);

    List<FormSubmission> findByStatus(FormStatus status);

    List<FormSubmission> findByFormTypeAndStatus(String formType, FormStatus status);
}
