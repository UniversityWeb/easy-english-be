package com.universityweb.topic;

import com.universityweb.course.entity.Course;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.topic.entity.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findById(Long id);

    @Query("""
        SELECT c 
        FROM Course c
        WHERE c.topic.id = (SELECT ct.topic.id FROM Course ct WHERE ct.id = :courseId)
          AND c.id != :courseId
          AND c.status = 'PUBLISHED'
    """)
    List<Course> getRelatedCoursesByTopic(Long courseId, Pageable pageable);
}
