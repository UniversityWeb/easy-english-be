package com.universityweb.writingtask.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "writing_tasks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WritingTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String ownerUsername;
    String teacherUsername;

    String title;

    @Column(columnDefinition = "TEXT")
    String instructions;

    @Enumerated(EnumType.STRING)
    EDifficultyLevel level;

    @Column(columnDefinition = "TEXT")
    String submittedText;

    Integer score;

    @Column(columnDefinition = "TEXT")
    String feedback;

    @Enumerated(EnumType.STRING)
    EStatus status;

    LocalDateTime submittedAt = LocalDateTime.now();

    public enum EDifficultyLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED
    }

    public enum EStatus {
        DELETED,
        SUBMITTED,
        FEEDBACK_PROVIDED_BY_AI,
        FEEDBACK_PROVIDED_BY_TEACHER
    }
}
