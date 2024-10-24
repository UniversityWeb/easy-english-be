package com.universityweb.testquestion;

import com.universityweb.testquestion.entity.TestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestQuestionRepos extends JpaRepository<TestQuestion, Long> {
}
