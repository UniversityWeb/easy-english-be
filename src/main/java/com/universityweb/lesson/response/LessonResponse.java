package com.universityweb.lesson.response;

import com.universityweb.lesson.customenum.LessonType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse {
    private Long id;
    private Long sectionId;
    private String title;
    private LessonType type;
    private String content;
    private String contentUrl;
    private String description;
    private int duration;
    private Boolean isPreview;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDateTime createdAt;
}
