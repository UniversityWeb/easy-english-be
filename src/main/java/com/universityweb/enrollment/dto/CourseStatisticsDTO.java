package com.universityweb.enrollment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseStatisticsDTO {
    String courseTitle;
    int totalStudents;
    double averageProgress;
    double passedQuizzesPercentage;
    double passedLessonsPercentage;

    public CourseStatisticsDTO(String courseTitle, int totalStudents, double averageProgress,
                               double passedQuizzesPercentage, double passedLessonsPercentage) {
        this.courseTitle = courseTitle;
        this.totalStudents = totalStudents;
        this.averageProgress = averageProgress;
        this.passedQuizzesPercentage = passedQuizzesPercentage;
        this.passedLessonsPercentage = passedLessonsPercentage;
    }
}
