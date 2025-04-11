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

    @Query("SELECT COUNT(DISTINCT tr.test.id) " +
            "FROM TestResult tr " +
            "JOIN tr.test t " +
            "JOIN t.section s " +
            "JOIN s.course c " +
            "WHERE tr.user.username = :username " +
            "AND c.id = :courseId " +
            "AND tr.status = :status")
    int countDistinctTestsByUsernameAndCourseId(@Param("username") String username,
                                                 @Param("courseId") Long courseId,
                                                 @Param("status") TestResult.EStatus status);

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

    @Query(value = """
    SELECT AVG(passed_percentage)
        FROM (
            SELECT
                tr.username,
                100.0 * SUM(CASE WHEN tr.status = 'DONE' THEN 1 ELSE 0 END) / COUNT(*) AS passed_percentage
            FROM test_results tr
            JOIN tests t ON tr.id = t.id
            JOIN sections s ON t.id = s.id
            WHERE s.course_id = 96
            GROUP BY tr.username
        ) AS student_passed_percentages;
    """, nativeQuery = true)
    Double getAveragePassedPercentageByCourseId(@Param("courseId") Long courseId);
}
