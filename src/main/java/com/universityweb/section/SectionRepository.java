package com.universityweb.section;

import com.universityweb.section.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findById(Long id);

    @Query("""
        SELECT s FROM Section s WHERE s.course.id = :courseId 
        AND s.status <> 'DELETED'
        ORDER BY s.id ASC
    """)
    List<Section> findByCourseId(Long courseId);
}
