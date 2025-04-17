package com.universityweb.writingtask.req;

import com.universityweb.writingtask.entity.WritingTask;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WritingTaskFilterReq {
    Long sectionId;

    String title;
    WritingTask.EDifficultyLevel level;
    WritingTask.EStatus status;

    @NotNull(message = "Page number is required")
    @Min(value = 0, message = "Page number must be 0 or greater")
    @Schema(description = "Page number for pagination", example = "0")
    Integer pageNumber = 0;

    @NotNull(message = "Size is required")
    @Min(value = 1, message = "Size must be at least 1")
    @Schema(description = "Number of items per page for pagination", example = "8")
    Integer size = 8;
}
