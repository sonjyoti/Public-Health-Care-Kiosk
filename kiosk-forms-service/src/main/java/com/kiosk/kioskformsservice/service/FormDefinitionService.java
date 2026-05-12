package com.kiosk.kioskformsservice.service;

import com.kiosk.kioskformsservice.dto.FormDefinitionRequest;
import com.kiosk.kioskformsservice.dto.FormDefinitionResponse;
import com.kiosk.kioskformsservice.exception.FormDefinitionNotFoundException;
import com.kiosk.kioskformsservice.model.FormDefinition;
import com.kiosk.kioskformsservice.repository.FormDefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormDefinitionService {

    private final FormDefinitionRepository formDefinitionRepository;

    public FormDefinitionResponse createFormDefinition(FormDefinitionRequest request){
        FormDefinition definition = FormDefinition.builder()
                .formType(request.getFormType().toUpperCase())
                .displayName(request.getDisplayName())
                .displayNameHi(request.getDisplayNameHi())
                .displayNameAs(request.getDisplayNameAs())
                .fieldSchema(request.getFieldSchema())
                .isActive(true)
                .build();

        FormDefinition saved = formDefinitionRepository.save(definition);
        log.info("Form definition created: {}", saved.getFormType());
        return mapToResponse(saved);
    }

    public List<FormDefinitionResponse> getAllActiveForms(){
        return formDefinitionRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public  FormDefinitionResponse getByFormType(String formType){
        return formDefinitionRepository
                .findByFormTypeAndIsActiveTrue(formType.toUpperCase())
                .map(this::mapToResponse)
                .orElseThrow(() -> new FormDefinitionNotFoundException(
                        formType
                ));
    }

    public void deactivateForm(String formType){
        FormDefinition definition = formDefinitionRepository
                .findById(formType.toUpperCase())
                .orElseThrow(()  -> new FormDefinitionNotFoundException(formType));
        definition.setActive(false);
        formDefinitionRepository.save(definition);
        log.info("Form definition deactivated: {}", formType);
    }

    private FormDefinitionResponse mapToResponse(FormDefinition def){
        return FormDefinitionResponse.builder()
                .formType(def.getFormType())
                .displayName(def.getDisplayName())
                .displayNameHi(def.getDisplayNameHi())
                .displayNameAs(def.getDisplayNameAs())
                .fieldSchema(def.getFieldSchema())
                .isActive(def.isActive())
                .build();
    }
}
