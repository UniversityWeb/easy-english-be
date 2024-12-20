package com.universityweb.lesson.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LessonRequest {
    Long id;

    Long sectionId;

    String title;

    String type;

    String content;

    String contentUrl;

    String description;

    int duration;

    Boolean isPreview;

    LocalDate startDate;

    LocalTime startTime;

    LocalDateTime createdAt;
}
