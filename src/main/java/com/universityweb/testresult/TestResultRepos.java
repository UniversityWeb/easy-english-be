package com.universityweb.testresult;

import com.universityweb.testresult.entity.TestResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestResultRepos extends JpaRepository<TestResult, Long> {
    Page<TestResult> findByUser_Username(String username, Pageable pageable);
    Optional<TestResult> findByUser_UsernameAndTest_Id(String username, Long testId);
}
