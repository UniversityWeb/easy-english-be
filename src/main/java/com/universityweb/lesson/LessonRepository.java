package com.universityweb.lesson;

import com.universityweb.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("""
        SELECT l FROM Lesson l
        WHERE l.section.id = :sectionId
        AND l.isDeleted = false
        ORDER BY l.createdAt ASC
    """)
    List<Lesson> findBySectionId(Long sectionId);

    @Query("SELECT l.id FROM Lesson l JOIN l.section s WHERE s.course.id = :courseId")
    List<Long> findLessonIdsByCourseId(@Param("courseId") Long courseId);
}
