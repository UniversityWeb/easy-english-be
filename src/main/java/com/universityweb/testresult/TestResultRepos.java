package com.universityweb.testresult;

import com.universityweb.testresult.entity.TestResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepos extends JpaRepository<TestResult, Long> {
    Page<TestResult> findByUser_UsernameAndTest_IdOrderByFinishedAtDesc(String username, Long testId, Pageable pageable);

    List<TestResult> findByUser_UsernameAndTest_IdOrderByFinishedAtDesc(String username, Long testId);
}
