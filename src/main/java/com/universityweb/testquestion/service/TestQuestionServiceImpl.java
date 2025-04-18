package com.universityweb.testquestion.service;

import com.universityweb.common.exception.ResourceNotFoundException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.questiongroup.service.QuestionGroupService;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.service.TestPartService;
import com.universityweb.testquestion.AddQuizQuestionRequest;
import com.universityweb.testquestion.TestQuestionMapper;
import com.universityweb.testquestion.TestQuestionRepos;
import com.universityweb.testquestion.dto.TestQuestionDTO;
import com.universityweb.testquestion.entity.TestQuestion;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestQuestionServiceImpl
        extends BaseServiceImpl<TestQuestion, TestQuestionDTO, Long, TestQuestionRepos, TestQuestionMapper>
        implements TestQuestionService {

    private final QuestionGroupService questionGroupService;
    private final TestPartService testPartService;

    @Autowired
    public TestQuestionServiceImpl(
            TestQuestionRepos repository,
            QuestionGroupService questionGroupService,
            TestPartService testPartService) {
        super(repository, TestQuestionMapper.INSTANCE);
        this.questionGroupService = questionGroupService;
        this.testPartService = testPartService;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new ResourceNotFoundException("TestQuestion Not Found with id=" + id);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(TestQuestion entity, TestQuestionDTO dto) {
        entity.setQuestionGroup(questionGroupService.getEntityById(dto.questionGroupId()));
    }

    @Transactional
    @Override
    public TestQuestionDTO update(Long id, TestQuestionDTO dto) {
        TestQuestion testQuestion = getEntityById(id);
        if (!testQuestion.getType().equals(dto.type())) {
            testQuestion.clearFields();
        }

        testQuestion.setType(dto.type());
        testQuestion.setTitle(dto.title());
        testQuestion.setDescription(dto.description());
        testQuestion.setOptions(dto.options());
        testQuestion.setCorrectAnswers(dto.correctAnswers());

        return savedAndConvertToDTO(testQuestion);
    }

    @Override
    public List<TestQuestionDTO> getByQuestionGroupId(Long questionGroupId) {
        List<TestQuestion> questions = repository.findByQuestionGroupIdOrderByOrdinalNumberAsc(questionGroupId);
        return mapper.toDTOs(questions);
    }

    @Override
    @Transactional
    public TestQuestionDTO createNewQuestionForQuizType(AddQuizQuestionRequest request) {
        Long testId = request.getTestId();
        @NotNull TestPart testPart = testPartService.getFirstOrCreateTestPartByTestId(testId);
        @NotNull QuestionGroup questionGroup = questionGroupService.getFirstOrCreateGroupByTestPartId(testPart.getId());
        TestQuestion testQuestion = mapper.toEntity(request);
        testQuestion.setQuestionGroup(questionGroup);
        return savedAndConvertToDTO(testQuestion);
    }

    @Override
    public List<TestQuestionDTO> getAllQuestionsForQuizType(Long testId) {
        @NotNull TestPart testPart = testPartService.getFirstOrCreateTestPartByTestId(testId);
        @NotNull QuestionGroup questionGroup = questionGroupService.getFirstOrCreateGroupByTestPartId(testPart.getId());
        return getByQuestionGroupId(questionGroup.getId());
    }

    @Override
    public int getNumberOfQuestions(Long testId) {
        return repository.countByTestId(testId);
    }

    @Override
    public List<TestQuestion> findByTestId(Long testId) {
        return repository.findByTestId(testId);
    }

    @Override
    @Transactional
    public void swapTwoQuestions(Long questionId1, Long questionId2) {
        TestQuestion question1 = getEntityById(questionId1);
        TestQuestion question2 = getEntityById(questionId2);

        Integer tempOrdinalNumber = question1.getOrdinalNumber();
        question1.setOrdinalNumber(question2.getOrdinalNumber());
        question2.setOrdinalNumber(tempOrdinalNumber);

        repository.save(question1);
        repository.save(question2);
    }

    @Override
    public void softDelete(Long id) {
        repository.deleteById(id);
    }
}
