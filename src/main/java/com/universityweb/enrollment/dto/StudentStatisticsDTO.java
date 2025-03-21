package com.universityweb.enrollment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentStatisticsDTO {
    String username;
    String fullName;
    String email;
    LocalDateTime startedDate;
    int lessonsPassed;
    int totalLessons;
    int quizzesPassed;
    int totalQuizzes;
    double progress;

    public StudentStatisticsDTO(String username, String fullName, String email, LocalDateTime startedDate,
                                Long lessonsPassed, Long totalLessons, Long quizzesPassed, Long totalQuizzes, double progress) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.startedDate = startedDate;
        this.lessonsPassed = lessonsPassed != null ? lessonsPassed.intValue() : 0;
        this.totalLessons = totalLessons != null ? totalLessons.intValue() : 0;
        this.quizzesPassed = quizzesPassed != null ? quizzesPassed.intValue() : 0;
        this.totalQuizzes = totalQuizzes != null ? totalQuizzes.intValue() : 0;
        this.progress = progress;
    }
}
