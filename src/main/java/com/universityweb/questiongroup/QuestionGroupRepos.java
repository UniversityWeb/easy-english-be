package com.universityweb.questiongroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionGroupRepos extends JpaRepository<QuestionGroup, Long> {
}