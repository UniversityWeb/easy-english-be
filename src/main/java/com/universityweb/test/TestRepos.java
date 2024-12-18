package com.universityweb.test;

import com.universityweb.test.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepos extends JpaRepository<Test, Long> {
    @Query("""
            SELECT t FROM Test t
            WHERE t.section.id = :sectionId AND t.status <> 'DELETED'
            ORDER BY t.createdAt ASC
            """)
    List<Test> findBySectionId(Long sectionId);

    @Query("""
            SELECT c.id FROM Test t JOIN t.section s JOIN s.course c 
            WHERE t.id = :id
            """)
    Long findCourseIdByTestId(Long id);
}
