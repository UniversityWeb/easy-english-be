package com.universityweb.lessontracker.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateLessonTrackerReq {
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private String username;
    private Long lessonId;
}
