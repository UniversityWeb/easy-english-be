package com.universityweb.testquestion;

import com.universityweb.testquestion.entity.TestQuestion;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestQuestionRepos extends JpaRepository<TestQuestion, Long> {
    List<TestQuestion> findByQuestionGroupId(Long questionGroupId, Sort sort);

    @Query("SELECT COUNT(q) " +
            "FROM TestQuestion q " +
            "JOIN q.questionGroup g " +
            "JOIN g.testPart p " +
            "WHERE p.test.id = :testId")
    int countByTestId(@Param("testId") Long testId);
}
