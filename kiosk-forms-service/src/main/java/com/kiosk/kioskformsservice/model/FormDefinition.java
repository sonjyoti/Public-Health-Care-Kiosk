package com.kiosk.kioskformsservice.model;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "form_definitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormDefinition {

    @Id
    @Column(name = "form_type", length = 50)
    private String formType;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "display_name_hi")
    private String displayNameHi;

    @Column(name = "display_name_as")
    private String displayNameAs;

    @Type(JsonBinaryType.class)
    @Column(name = "field_schema", columnDefinition = "jsonb")
    private JsonBinaryType fieldSchema;

    @Column(name = "is_active")
    private boolean isActive = true;
}
