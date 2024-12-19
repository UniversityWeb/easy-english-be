package com.universityweb.statistics.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseFilterReq {
    String ownerUsername;
    Integer month;
    Integer year;

    @Schema(description = "Page number for pagination", example = "0")
    Integer page;

    @Schema(description = "Number of items per page for pagination", example = "10")
    Integer size;
}
