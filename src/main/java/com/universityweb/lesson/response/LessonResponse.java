package com.universityweb.lesson.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.universityweb.drip.dto.PrevDripDTO;
import com.universityweb.lesson.customenum.LessonType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {
    Long id;
    Long sectionId;
    String title;
    LessonType type;
    String content;
    String contentUrl;
    String description;
    int duration;
    Boolean isPreview;
    LocalDate startDate;
    LocalTime startTime;
    LocalDateTime createdAt;

    boolean isCompleted;

    @JsonProperty("isLocked")
    boolean isLocked;

    Set<PrevDripDTO> prevDrips;
}
