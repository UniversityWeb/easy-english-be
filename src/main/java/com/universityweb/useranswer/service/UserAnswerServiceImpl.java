package com.universityweb.useranswer.service;

import com.universityweb.common.exception.ResourceNotFoundException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.testquestion.service.TestQuestionService;
import com.universityweb.testresult.service.TestResultService;
import com.universityweb.useranswer.UserAnswerMapper;
import com.universityweb.useranswer.UserAnswerRepos;
import com.universityweb.useranswer.dto.UserAnswerDTO;
import com.universityweb.useranswer.entity.UserAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAnswerServiceImpl
        extends BaseServiceImpl<UserAnswer, UserAnswerDTO, Long, UserAnswerRepos, UserAnswerMapper>
        implements UserAnswerService {

    private final TestQuestionService testQuestionService;
    private final TestResultService testResultService;

    @Autowired
    public UserAnswerServiceImpl(
            UserAnswerRepos repository,
            UserAnswerMapper mapper,
            TestQuestionService testQuestionService,
            TestResultService testResultService
    ) {
        super(repository, mapper);
        this.testQuestionService = testQuestionService;
        this.testResultService = testResultService;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new ResourceNotFoundException("Could not find user answer with id " + id);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(UserAnswer entity, UserAnswerDTO dto) {
        entity.setTestQuestion( testQuestionService.getEntityById(dto.getTestQuestionId()) );
        entity.setTestResult( testResultService.getEntityById(dto.getTestResultId()) );
    }

    @Transactional
    @Override
    public UserAnswerDTO update(Long id, UserAnswerDTO dto) {
        UserAnswer userAnswer = getEntityById(id);

        userAnswer.setAnswers(dto.getAnswers());
        userAnswer.setIsCorrect(dto.getIsCorrect());

        UserAnswer saved = repository.save(userAnswer);
        return mapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {}
}
