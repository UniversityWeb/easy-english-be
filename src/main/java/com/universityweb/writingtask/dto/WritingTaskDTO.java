package com.universityweb.writingtask.dto;

import com.universityweb.writingtask.entity.WritingTask;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WritingTaskDTO {
    Long id;
    String ownerUsername;
    String teacherUsername;
    String title;
    String instructions;
    WritingTask.EDifficultyLevel level;
    String submittedText;
    Integer score;
    String feedback;
    WritingTask.EStatus status;
    LocalDateTime submittedAt;
}
