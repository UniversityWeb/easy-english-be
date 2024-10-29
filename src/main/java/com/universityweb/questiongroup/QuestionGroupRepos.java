package com.universityweb.questiongroup;

import com.universityweb.questiongroup.entity.QuestionGroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionGroupRepos extends JpaRepository<QuestionGroup, Long> {
    List<QuestionGroup> findByTestPartId(Long testPartId, Sort sort);
}
