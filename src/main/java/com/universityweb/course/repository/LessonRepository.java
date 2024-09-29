package com.universityweb.course.repository;

import com.universityweb.course.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findBySectionId(int sectionId);
    Lesson findById(int id);
}
