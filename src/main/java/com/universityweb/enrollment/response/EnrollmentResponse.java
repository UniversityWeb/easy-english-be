package com.universityweb.enrollment.response;

import com.universityweb.enrollment.entity.Enrollment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record EnrollmentResponse (
        @Schema(description = "Unique identifier for the enrollment", example = "1")
        Long id,

        @Schema(description = "Progress of the enrollment in percentage", example = "85.5")
        double progress,

        @Schema(description = "Status of the enrollment", example = "ACTIVE")
        Enrollment.EStatus status,

        @Schema(description = "Type of enrollment", example = "PAID")
        Enrollment.EType type,

        @Schema(description = "Date and time when the enrollment was created", example = "2023-10-01T12:34:56")
        LocalDateTime createdAt,

        @Schema(description = "Date and time of the last access", example = "2023-10-10T15:20:30")
        LocalDateTime lastAccessed,

        @Schema(description = "Username of the enrolled user", example = "john.doe")
        String username,

        @Schema(description = "Unique identifier for the course", example = "101")
        Long courseId
) {}
