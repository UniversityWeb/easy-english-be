package com.universityweb.test;

import com.universityweb.test.entity.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepos extends JpaRepository<Test, Long> {
    @Query("SELECT t FROM Test t " +
            "WHERE t.section.id = :sectionId AND t.status <> 'DELETED'")
    List<Test> findBySectionId(Long sectionId, Sort sort);
}
