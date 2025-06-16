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

    Long sectionId;

    String title;
    String instructions;
    WritingTask.EDifficultyLevel level;
    WritingTask.EStatus status;
    LocalDateTime startDate;
    LocalDateTime endDate;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
