package com.universityweb.level;

import com.universityweb.course.entity.Course;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.level.entity.Level;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {
    Optional<Level> findById(Long id);

    List<Level> findByTopicId(Long topicId);

    @Query("""
        SELECT c 
        FROM Course c
        WHERE c.level.id = (SELECT cl.level.id FROM Course cl WHERE cl.id = :courseId)
          AND c.id != :courseId
          AND c.status = 'PUBLISHED'
        ORDER BY function('random')
    """)
    List<Course> getRelatedCoursesByLevel(Long courseId, Pageable pageable);
}
