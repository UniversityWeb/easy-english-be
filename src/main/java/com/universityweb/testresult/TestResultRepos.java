package com.universityweb.testresult;

import com.universityweb.testresult.entity.TestResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepos extends JpaRepository<TestResult, Long> {
    List<TestResult> findByUser_UsernameAndTest_IdOrderByFinishedAtDesc(String username, Long testId);

    Page<TestResult> findByTest_IdOrderByFinishedAtDesc(Long testId, Pageable pageable);

    @Query("""
        SELECT DISTINCT tr FROM TestResult tr 
        WHERE tr.user.username = :username 
        AND tr.test.section.course.id = :courseId 
        AND tr.status = :status
        AND tr.isDeleted = false
        AND tr.finishedAt = (
            SELECT MAX(subTr.finishedAt) 
            FROM TestResult subTr 
            WHERE subTr.user.username = tr.user.username 
            AND subTr.test.section.course.id = tr.test.section.course.id 
            AND subTr.status = tr.status 
            AND subTr.isDeleted = false
        )
        """)
    List<TestResult> findByUserUsernameAndTestSectionCourseIdAndStatus(String username, Long courseId, TestResult.EStatus status);

    @Query("""
        SELECT COUNT(tr) > 0
        FROM TestResult tr
        WHERE tr.user.username = :username
        AND tr.test.id = :testId
        AND tr.status = 'DONE'
        AND tr.isDeleted = false
        """)
    boolean isTestDone(String username, Long testId);

    @Query("""
        SELECT tr FROM TestResult tr
        JOIN tr.user u
        WHERE u.username = :username
        AND tr.isDeleted = false
    """)
    Page<TestResult> findAllByUsername(
            @Param("username") String username,
            Pageable pageable);
}
