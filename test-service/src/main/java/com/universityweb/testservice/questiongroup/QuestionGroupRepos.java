package com.universityweb.testservice.questiongroup;

import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.testpart.entity.TestPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionGroupRepos extends JpaRepository<QuestionGroup, Long> {
    @Query("SELECT qg FROM QuestionGroup qg " +
            "WHERE qg.testPart.id = :testPartId AND qg.isDeleted = false")
    List<QuestionGroup> findByTestPartId(Long testPartId);

    @Query("SELECT qg FROM QuestionGroup qg " +
            "WHERE qg.testPart.id = :testPartId ORDER BY qg.ordinalNumber ASC")
    QuestionGroup getFirstGroupByTestPartId(Long testPartId);

    List<QuestionGroup> findByTestPartOrderByOrdinalNumberAsc(TestPart testPart);
}
