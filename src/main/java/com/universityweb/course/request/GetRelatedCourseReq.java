package com.universityweb.course.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetRelatedCourseReq {
    Long courseId;
    int numberOfCourses;
    EType type;

    public enum EType {
        LEVEL,
        TOPIC
    }
}
