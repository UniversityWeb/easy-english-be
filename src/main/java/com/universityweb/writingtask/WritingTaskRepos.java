package com.universityweb.writingtask;

import com.universityweb.writingtask.entity.WritingTask;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WritingTaskRepos extends JpaRepository<WritingTask, Long> {
    @Query("""
        SELECT w FROM WritingTask w
        WHERE (:sectionId IS NULL OR w.sectionId = :sectionId)
          AND (:title IS NULL OR :title = '' OR LOWER(w.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:level IS NULL OR w.level = :level)
          AND (:status IS NULL OR w.status = :status)
    """)
    Page<WritingTask> findByFilters(
            @Param("sectionId") Long sectionId,
            @Param("title") String title,
            @Param("level") WritingTask.EDifficultyLevel level,
            @Param("status") WritingTask.EStatus status,
            Pageable pageable
    );
}
