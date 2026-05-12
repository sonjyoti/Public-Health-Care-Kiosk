package com.kiosk.kioskformsservice.service;

import com.kiosk.kioskformsservice.dto.FormSubmissionRequest;
import com.kiosk.kioskformsservice.dto.FormSubmissionResponse;
import com.kiosk.kioskformsservice.dto.StatusUpdateRequest;
import com.kiosk.kioskformsservice.exception.FormDefinitionNotFoundException;
import com.kiosk.kioskformsservice.exception.FormSubmissionNotFoundException;
import com.kiosk.kioskformsservice.exception.InvalidFormDataException;
import com.kiosk.kioskformsservice.model.FormStatus;
import com.kiosk.kioskformsservice.model.FormSubmission;
import com.kiosk.kioskformsservice.repository.FormDefinitionRepository;
import com.kiosk.kioskformsservice.repository.FormSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormSubmissionService {
    private final FormSubmissionRepository submissionRepository;
    private final FormDefinitionRepository definitionRepository;

    public FormSubmissionResponse submit(FormSubmissionRequest request){

        //validate form type exists and is active
        definitionRepository
                .findByFormTypeAndIsActiveTrue(
                        request.getFormType().toUpperCase())
                .orElseThrow(() -> new FormDefinitionNotFoundException(
                        request.getFormType()
                ));

        // validate form data is not empty JSON
        if (request.getFormData() == null
        || request.getFormData().isBlank()
        || request.getFormData().equals("{}")){
            throw new InvalidFormDataException(
                    "Form data can't be empty"
            );
        }

        FormSubmission submission = FormSubmission.builder()
                .formType(request.getFormType().toUpperCase())
                .sessionToken(request.getSessionToken())
                .formData(request.getFormData())
                .status(FormStatus.SUBMITTED)
                .build();

        FormSubmission saved =  submissionRepository.save(submission);
        log.info("Form submitted: id={} type={}", saved.getId(), saved.getFormType());
        return mapToResponse(saved);
    }

    public FormSubmissionResponse getById(String id){
        return submissionRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new FormSubmissionNotFoundException(id));
    }

    public List<FormSubmissionResponse> getSessionToken(String sessionToken){
        return submissionRepository.findBySessionToken(sessionToken)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FormSubmissionResponse> getByFormType(String formType){
        return submissionRepository.findByFormType(formType.toUpperCase())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FormSubmissionResponse> getByStatus(FormStatus status){
        return submissionRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FormSubmissionResponse updateStatus(String id, StatusUpdateRequest request){
        FormSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new FormSubmissionNotFoundException(id));

        submission.setStatus(request.getStatus());

        if (request.getStatus() == FormStatus.COMPLETED
        || request.getStatus() == FormStatus.REJECTED){
            submission.setProcessedAt(LocalDateTime.now());
        }

        FormSubmission updated = submissionRepository.save(submission);
        log.info("Form submission {} status updated to {}", updated.getId(), request.getStatus());
        return mapToResponse(updated);
    }

    private FormSubmissionResponse mapToResponse(FormSubmission submission){
        return FormSubmissionResponse.builder()
                .id(submission.getId())
                .formType(submission.getFormType())
                .sessionToken(submission.getSessionToken())
                .formData(submission.getFormData())
                .status(submission.getStatus().name())
                .submittedAt(submission.getSubmittedAt())
                .processedAt(submission.getProcessedAt())
                .build();
    }
}
