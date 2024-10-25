package com.universityweb.useranswer;

import com.universityweb.useranswer.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnswerRepos extends JpaRepository<UserAnswer, Long> {
}
