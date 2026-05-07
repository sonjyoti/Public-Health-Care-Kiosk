package model;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "form_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "form_type", nullable = false)
    private String formType;

    @Column(name = "session_token", nullable = false)
    private String sessionToken;

    @Type(JsonBinaryType.class)
    @Column(name = "form_data", columnDefinition = "jsonb", nullable = false)
    private String formData;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FormStatus status;

    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = FormStatus.SUBMITTED;
        }
        if (this.sessionToken == null) {
            this.sessionToken = UUID.randomUUID().toString();
        }
    }

}
