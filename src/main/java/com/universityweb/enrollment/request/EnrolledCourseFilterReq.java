package com.universityweb.enrollment.request;

import com.universityweb.enrollment.entity.Enrollment;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrolledCourseFilterReq {
    Long favoriteId;
    Long id;
    String title;
    Long levelId;
    Long topicId;
    int duration;
    int countView;
    String createdAt;
    String notice;
    String ownerUsername;
    Double rating;
    List<Long> categoryIds;

    double progress;
    Enrollment.EStatus enrollmentStatus;
    Enrollment.EType enrollmentType;

    int page = 0;
    int size = 10;
}
