package com.universityweb.course.section;

import com.universityweb.course.section.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findById(Long id);

    List<Section> findByCourseId(Long courseId);
}
