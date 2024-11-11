package com.universityweb.testpart.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.questiongroup.QuestionGroupRepos;
import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.review.service.ReviewServiceImpl;
import com.universityweb.test.entity.Test;
import com.universityweb.test.service.TestService;
import com.universityweb.testpart.TestPartRepos;
import com.universityweb.testpart.dto.TestPartDTO;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.mapper.TestPartMapper;
import com.universityweb.testquestion.TestQuestionRepos;
import com.universityweb.testquestion.entity.TestQuestion;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestPartServiceImpl 
        extends BaseServiceImpl<TestPart, TestPartDTO, Long, TestPartRepos, TestPartMapper> 
        implements TestPartService {

    private final TestService testService;
    private final QuestionGroupRepos questionGroupRepos;
    private final TestQuestionRepos testQuestionRepos;

    @Autowired
    protected TestPartServiceImpl(
            TestPartRepos repository,
            TestPartMapper mapper,
            TestService testService,
            QuestionGroupRepos questionGroupRepos,
            TestQuestionRepos testQuestionRepos) {
        super(repository, mapper);
        this.testService = testService;
        this.questionGroupRepos = questionGroupRepos;
        this.testQuestionRepos = testQuestionRepos;
    }

    @Override
    public List<TestPartDTO> getTestPartsByTestId(Long testId) {
        Sort sort = Sort.by(Sort.Order.asc("ordinalNumber"));
        List<TestPart> testParts = repository.findByTestId(testId, sort);
        return mapper.toDTOs(testParts);
    }

    @Override
    public @NotNull TestPart getFirstOrCreateTestPartByTestId(Long testId) {
        TestPart testPart = repository.getFirstTestPartByTestId(testId);
        if (testPart != null) {
            return testPart;
        }

        Test test = testService.getEntityById(testId);
        TestPart newTestPart = TestPart.builder()
                .title("Default Title")
                .ordinalNumber(1)
                .isDeleted(false)
                .test(test)
                .build();
        return repository.save(newTestPart);
    }

    @Override
    @Transactional
    public void swapTwoParts(Long partId1, Long partId2) {
        TestPart part1 = getEntityById(partId1);
        TestPart part2 = getEntityById(partId2);

        Integer tempOrdinalNumber = part1.getOrdinalNumber();
        part1.setOrdinalNumber(part2.getOrdinalNumber());
        part2.setOrdinalNumber(tempOrdinalNumber);

        repository.save(part1);
        repository.save(part2);

        reassignOrdinalNumbersAfterPartSwap(part1, part2);
    }

    @Override
    public TestPartDTO update(Long id, TestPartDTO dto) {
        TestPart existingTestPart = getEntityById(id);

        existingTestPart.setTitle(dto.title());
        existingTestPart.setReadingPassage(dto.readingPassage());
        existingTestPart.setOrdinalNumber(dto.ordinalNumber());

        TestPart updatedTestPart = repository.save(existingTestPart);
        return mapper.toDTO(updatedTestPart);
    }

    @Override
    public void softDelete(Long id) {
        super.softDelete(id);

        TestPart existingTestPart = getEntityById(id);
        existingTestPart.setIsDeleted(true);
        repository.save(existingTestPart);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find any test parts with id" + id);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(TestPart entity, TestPartDTO dto) {
        entity.setIsDeleted(false);
        entity.setTest(testService.getEntityById(dto.testId()));
    }

    private void reassignOrdinalNumbersAfterPartSwap(TestPart part1, TestPart part2) {
        // Get all QuestionGroups for both parts
        List<QuestionGroup> part1Groups = questionGroupRepos.findByTestPartOrderByOrdinalNumberAsc(part1);
        List<QuestionGroup> part2Groups = questionGroupRepos.findByTestPartOrderByOrdinalNumberAsc(part2);

        // Reset ordinal numbers for both part1 and part2 groups and questions
        int ordinalNumber = 1;

        // Update QuestionGroups and their TestQuestions for part1
        ordinalNumber = updateGroupsAndQuestions(part1Groups, ordinalNumber);

        // Continue updating QuestionGroups and their TestQuestions for part2
        updateGroupsAndQuestions(part2Groups, ordinalNumber);
    }

    private int updateGroupsAndQuestions(List<QuestionGroup> groups, int startingOrdinal) {
        int groupOrdinal = startingOrdinal;

        for (QuestionGroup group : groups) {
            group.setOrdinalNumber(groupOrdinal++);
            questionGroupRepos.save(group);

            // Update the TestQuestions within this QuestionGroup
            List<TestQuestion> questions = testQuestionRepos.findByQuestionGroupOrderByOrdinalNumberAsc(group);

            // Set the TestQuestions' ordinal numbers to be continuous
            for (TestQuestion question : questions) {
                question.setOrdinalNumber(startingOrdinal++);
                testQuestionRepos.save(question);
            }
        }
        return startingOrdinal;  // Return the next available ordinal number for continuity
    }
}
