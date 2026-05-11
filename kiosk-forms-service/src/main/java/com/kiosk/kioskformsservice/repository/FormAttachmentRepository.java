package com.kiosk.kioskformsservice.repository;

import com.kiosk.kioskformsservice.model.FormAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormAttachmentRepository extends JpaRepository<FormAttachment,String> {
    List<FormAttachment> findBySubmissionId(String submissionId);
}
