package com.universityweb.writingresult.dto;

import com.universityweb.writingresult.entity.WritingResult;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WritingResultDTO {
    Long id;

    // foreign keys
    Long writingTaskId;
    String ownerUsername;

    String submittedText;
    String feedback;
    WritingResult.EStatus status;
    LocalDateTime submittedAt;
}
