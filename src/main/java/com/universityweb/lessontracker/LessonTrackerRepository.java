package com.universityweb.lessontracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonTrackerRepository extends JpaRepository<LessonTracker, Long> {
    Optional<LessonTracker> findByUser_UsernameAndLesson_Id(String username, Long lessonId);

    List<LessonTracker> findByUserUsernameAndLessonSectionCourseIdAndIsCompletedTrue(String username, Long courseId);
}
