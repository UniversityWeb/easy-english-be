package com.universityweb.course.lesson.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse {
    private Long id;
    private String title;
    private String type;
    private String content;
    private String contentUrl;
    private String description;
    private int duration;
    private Boolean isPreview;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDateTime createdAt;
    private String createdBy;
}