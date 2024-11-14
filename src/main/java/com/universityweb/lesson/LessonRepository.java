package com.universityweb.lesson;

import com.universityweb.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("SELECT t FROM Test t " +
            "WHERE t.section.id = :sectionId AND t.status <> 'DELETED' " +
            "ORDER BY t.ordinalNumber ASC")
    List<Lesson> findBySectionId(Long sectionId);
}
