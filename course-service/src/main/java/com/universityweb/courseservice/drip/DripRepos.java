package com.universityweb.courseservice.drip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DripRepos extends JpaRepository<Drip, Long> {
    List<Drip> findAllByPrevId(Long prevId);

    List<Drip> findAllByCourseId(Long courseId);

    List<Drip> findAllByNextTypeAndNextId(Drip.ESourceType targetType, Long targetId);

    void deleteByCourseId(Long courseId);

    @Query("""
            SELECT d FROM Drip d WHERE d.nextId = :nextId AND d.nextType = :nextType
            """)
    List<Drip> findDripByNextId(Long nextId, Drip.ESourceType nextType);

    @Query("""
            SELECT d FROM Drip d 
            LEFT JOIN Lesson l ON d.prevType = 'LESSON' AND d.prevId = l.id
            LEFT JOIN Test t ON d.prevType = 'TEST' AND d.prevId = t.id
            WHERE d.nextId = :nextId AND d.nextType = :nextType
               
            """)
    List<Drip> findUnfinishedPrevDripByNextId(Long nextId, Drip.ESourceType nextType);
}
