package com.universityweb.testquestion;

import com.universityweb.testquestion.entity.TestQuestion;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestQuestionRepos extends JpaRepository<TestQuestion, Long> {
    List<TestQuestion> findByQuestionGroupId(Long questionGroupId, Sort sort);
}
