package com.universityweb.courseservice.lessontracker.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LessonTrackerDTO {
    private Long id;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private String username;
    private Long lessonId;
}
