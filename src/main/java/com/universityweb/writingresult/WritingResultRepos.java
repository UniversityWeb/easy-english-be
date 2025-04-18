package com.universityweb.writingresult;

import com.universityweb.writingresult.entity.WritingResult;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WritingResultRepos extends JpaRepository<WritingResult, Long> {
    @Query("""
        SELECT w FROM WritingResult w
        WHERE (:writingTaskId IS NULL OR w.writingTaskId = :writingTaskId)
          AND (:ownerUsername IS NULL OR :ownerUsername = '' OR w.ownerUsername = :ownerUsername)
          AND (:status IS NULL OR w.status = :status)
    """)
    Page<WritingResult> findByFilters(
            @Param("writingTaskId") Long writingTaskId,
            @Param("ownerUsername") String ownerUsername,
            @Param("status") WritingResult.EStatus status,
            Pageable pageable
    );
}
