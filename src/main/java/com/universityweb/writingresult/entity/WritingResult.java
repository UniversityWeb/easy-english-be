package com.universityweb.writingresult.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "writing_results")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Where(clause = "status != 'DELETED'")
public class WritingResult implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // foreign keys
    Long writingTaskId;
    String ownerUsername;

    @Column(columnDefinition = "TEXT")
    String submittedText;

    @Column(columnDefinition = "TEXT")
    String feedback;

    @Enumerated(EnumType.STRING)
    EStatus status;

    @CreationTimestamp
    LocalDateTime submittedAt;

    public enum EStatus {
        DELETED,
        DRAFT,
        SUBMITTED,
        FEEDBACK_PROVIDED,
    }
}
