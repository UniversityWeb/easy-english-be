package com.universityweb.lesson;

import com.universityweb.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("SELECT l FROM Lesson l " +
            "WHERE l.section.id = :sectionId " +
            "ORDER BY l.ordinalNumber ASC")
    List<Lesson> findBySectionId(Long sectionId);
}
