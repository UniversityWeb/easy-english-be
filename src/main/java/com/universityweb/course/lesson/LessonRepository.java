package com.universityweb.course.lesson;

import com.universityweb.course.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findBySectionId(Long sectionId);
    Optional<Lesson> findById(Long id);
}