package com.universityweb.courseservice.lessontracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonTrackerRepository extends JpaRepository<LessonTracker, Long> {
    Optional<LessonTracker> findByUser_UsernameAndLesson_Id(String username, Long lessonId);

    List<LessonTracker> findByUserUsernameAndLessonSectionCourseIdAndIsCompletedTrue(String username, Long courseId);

    @Query("""
        SELECT COUNT(lt) > 0 
        FROM LessonTracker lt  
        WHERE lt.user.username = :username 
        AND lt.lesson.id = :lessonId 
        AND lt.isCompleted = true 
        AND lt.isDeleted = false
    """)
    boolean isLessonCompleted(@Param("username") String username, @Param("lessonId") Long lessonId);
}
