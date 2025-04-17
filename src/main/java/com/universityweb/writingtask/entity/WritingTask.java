package com.universityweb.writingtask.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "writing_tasks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Where(clause = "status != 'DELETED'")
public class WritingTask implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // foreign keys
    Long sectionId;

    String title;

    @Column(columnDefinition = "TEXT")
    String instructions;

    @Enumerated(EnumType.STRING)
    EDifficultyLevel level;

    @Enumerated(EnumType.STRING)
    EStatus status;

    LocalDateTime startDate;
    LocalDateTime endDate;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    public enum EDifficultyLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED
    }

    public enum EStatus {
        DELETED,
        DRAFT,
        PUBLIC,
    }
}
