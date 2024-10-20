package com.universityweb.course.enrollment.request;

import com.universityweb.course.enrollment.entity.Enrollment;
import io.swagger.v3.oas.annotations.media.Schema;

public record AddEnrollmentRequest(
        @Schema(description = "Status of the enrollment", example = "ACTIVE")
        Enrollment.EStatus status,

        @Schema(description = "Type of enrollment", example = "PAID")
        Enrollment.EType type,

        @Schema(description = "Username of the user to be enrolled", example = "jane.doe")
        String username,

        @Schema(description = "Unique identifier for the course", example = "102")
        Long courseId
) { }
